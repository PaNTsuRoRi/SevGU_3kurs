import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

public class Assembler {
    private final String sourceCode;
    private final String LABEL_PATTERN = "^[a-zA-Z._?@$][\\w_?@$]*$";
    private final String NAME_PATTERN = "^[a-zA-Z]\\w*$";
    private final String BIN_PATTERN = "[0-1]+[bB]$";
    private final String OCTAL_PATTERN = "[0-7]+[oO]$";
    private final String DEC_PATTERN = "\\d+$";
    private final String HEX_PATTERN = "[a-fA-F0-9]+[hH]$";
    private final Map<CommandKey, Command> commandTable = Map.ofEntries(
    Map.entry(new CommandKey(CommandName.MOV, OperandType.REGISTER, OperandType.REGISTER),
    new Command(0x22, 1, 3, null, null, true, 2)),
    Map.entry(new CommandKey(CommandName.MOV, OperandType.REGISTER, OperandType.MEMORY),
    new Command(0x22, 1, 2, null, null, true, 4)),
    Map.entry(new CommandKey(CommandName.MOV, OperandType.MEMORY, OperandType.REGISTER),
    new Command(0x22, 0, 2, null, null, true, 4)),
    Map.entry(new CommandKey(CommandName.MOV, OperandType.REGISTER, OperandType.IMMEDIATE),
    new Command(0xB, null, null, null, null, true, 3)),
    Map.entry(new CommandKey(CommandName.TEST, OperandType.REGISTER, OperandType.REGISTER),
    new Command(0xE, 1, 3, null, null, true, 2)),
    Map.entry(new CommandKey(CommandName.TEST, OperandType.REGISTER, OperandType.MEMORY),
    new Command(0xE, 1, 2, null, null, true, 4)),
    Map.entry(new CommandKey(CommandName.TEST, OperandType.MEMORY, OperandType.REGISTER),
    new Command(0xE, 0, 2, null, null, true, 4)),
    Map.entry(new CommandKey(CommandName.TEST, OperandType.REGISTER, OperandType.IMMEDIATE),
    new Command(0x1E, null, null, null, null, true, 3)),
    Map.entry(new CommandKey(CommandName.DIV, OperandType.REGISTER, null),
    new Command(0x7B, null, 3, 2, null, true, 2)),
    Map.entry(new CommandKey(CommandName.JAE, OperandType.LABEL, null),
    new Command(0x78, null, null, null, null, null, 2)),
    Map.entry(new CommandKey(CommandName.INT, OperandType.IMMEDIATE, null),
    new Command(0xCD, null, null, null, null, null, 2)));
    private String segmentName;
    private Integer org;
    private Integer machineCodeLength;
    private ArrayList<String> errors;
    private HashMap<String, Integer> labelTable;
    private Set<Variable> varTable;
    private TreeMap<Integer, CodeLine> machineCode;
    public Assembler(String sourceCode) {
        this.sourceCode = sourceCode;
        errors = new ArrayList<>();
        machineCode = new TreeMap<>();
        labelTable = new HashMap<>();
        varTable = new HashSet<>();
        org = null;
        machineCodeLength = null;
    }
    public String compile() {
        String result = "";
        Map<Integer, CommandLine> codeToTranslate = analyze(parseSourceCode());
        String fileName = String.format("listing%d.txt", System.currentTimeMillis());
        String objFileName = String.format("obj%d.txt", System.currentTimeMillis());
        if (errors.isEmpty()) {
            translateCode(codeToTranslate);
            if (errors.isEmpty()) {
                String filePath = generateListing(fileName);
                result = String.format("Translation completed. Result saved to file:\n %s", filePath);
                generateObjectCodeFile(objFileName);
            }
        }
        if (!errors.isEmpty()) {
            String filePath = generateListingWithErrors(fileName);
            result = String.format("Translation Failed:\nResult saved to file:\n %s", filePath);
        }
        return result;
    }
    private ArrayList<String> parseSourceCode() {
        ArrayList<String> codeLines = new ArrayList<>();
        for (String line : sourceCode.split("\n", 0)) {
            codeLines.add(line.trim().replaceAll(" +", " "));
        }
        return codeLines;
    }
    private Map<Integer, CommandLine> analyze(ArrayList<String> lines) {
        Map<Integer, CommandLine> result = new HashMap<>();
        int machineCodeRelativeLineNum = 0;
        boolean isSegmentStarted = false;
        boolean isOrgDeclared = false;
        boolean isEndsDeclared = false;
        boolean isCodeEnded = false;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                continue;
            }
            if (StringUtils.containsIgnoreCase(line, Instruction.SEGMENT.name())) {
                analyzeSegmentLine(line, i);
                isSegmentStarted = true;
                continue;
            }
            if (!isSegmentStarted) {
                errors.add(String.format(ErrorType.SEG_EXPECTED.getText(), i + 1));
                isSegmentStarted = true;
            }
            if (StringUtils.containsIgnoreCase(line, Instruction.ORG.name())) {
                analyzeOrgLine(line, i);
                isOrgDeclared = true;
                continue;
            }
            if (!isOrgDeclared) {
                errors.add(String.format(ErrorType.ORG_EXPECTED.getText(), i + 1));
                isOrgDeclared = true;
            }
            if (line.contains(":")) {
                line = parseLineWithLabel(line, i, machineCodeRelativeLineNum);
                if (line.isBlank()) {
                    machineCode.put(i, new CodeLine(machineCodeRelativeLineNum, ""));
                    continue;
                }
            }
            if (StringUtils.containsIgnoreCase(line, Instruction.DW.name())) {
                int length = analyzeVarLine(line, i, machineCodeRelativeLineNum);
                machineCodeRelativeLineNum += length;
                continue;
            }
            if (StringUtils.containsIgnoreCase(line, Instruction.ENDS.name())) {
                analyzeEndsLine(line, i);
                machineCodeLength = machineCodeRelativeLineNum;
                isEndsDeclared = true;
                continue;
            }
            if (StringUtils.containsIgnoreCase(line, Instruction.END.name())) {
                if (!isEndsDeclared) {
                    errors.add(String.format(ErrorType.ENDS_EXPECTED.getText(), i + 1));
                }
                isCodeEnded = true;
                break;
            }
            if (EnumUtils.isValidEnumIgnoreCase(CommandName.class, line.substring(0, line.indexOf(" ")))) {
                CommandLine cmdLine = analyzeCommandLine(line, i, machineCodeRelativeLineNum);
                result.put(i, cmdLine);
                machineCodeRelativeLineNum += cmdLine != null ? cmdLine.command.length : 0;
                continue;
            }
            errors.add(String.format(ErrorType.WRONG_CMD.getText(), line.substring(0, line.indexOf(" ")), i + 1));
        }
        if (!isCodeEnded) {
            errors.add(String.format(ErrorType.END_EXPECTED.getText(), lines.size()));
        }
        return result;
    }
    private void translateCode(Map<Integer, CommandLine> codeToTranslate) {
        if (org != null) {
            for (Variable var : varTable) {
                var.address += org;
            }
            for (Map.Entry<Integer, CodeLine> line : machineCode.entrySet()) {
                line.getValue().machineCodeIdx += org;
            }
            for (Map.Entry<Integer, CommandLine> entry : codeToTranslate.entrySet()) {
                CommandLine cmdLine = entry.getValue();
                CommandName commandName = cmdLine.commandName;
                Operand op1 = cmdLine.op1;
                Operand op2 = cmdLine.op2;
                Command command = cmdLine.command;
                int w = 0;
                if (command.w != null) {
                    w = command.w ? 1 : 0;
                }
                int code = 0;
                int addrByte = 0;
                Integer[] data = null;
                switch (commandName) {
                    case MOV: case CMP: {
                        if (op2.type.equals(OperandType.IMMEDIATE)) {
                            if (commandName.equals(CommandName.MOV)) {
                                code = (command.code << 4) + (w << 3) + op1.reg.getCode();
                            } else {
                                code = (command.code << 1) + w;
                            }
                            data = getReversedWord(op2.val);
                            break;
                        }
                        code = (command.code << 2) + (command.d << 1) + w;
                        addrByte = (command.mod << 6) + (command.reg << 3) + command.rm;
                        Operand opToConvert;
                        if (op1.type.equals(OperandType.MEMORY)) {
                            opToConvert = op1;
                        } else {
                            opToConvert = op2;
                        }
                        if (opToConvert.val != null) {
                            data = getReversedWord(opToConvert.val);
                        } else {
                            Variable v = varTable.stream()
                            .filter(var -> var.name.equals(opToConvert.name))
                            .findFirst()
                            .orElse(null);
                            if (v != null) {
                                data = getReversedWord(v.address);
                            } else if (opToConvert.name != null){
                                errors.add(String.format(ErrorType.UNKNOWN_VAR.getText(), opToConvert.name,
                                entry.getKey() + 1));
                            }
                        }
                        break;
                    }
                    case INT: case JS: {
                        code = command.code;
                        if (commandName.equals(CommandName.INT)) {
                            addrByte = op1.val;
                        } else {
                            Integer labelIdx = labelTable.get(op1.name);
                            if (labelIdx != null) {
                                addrByte = (byte) (labelIdx - cmdLine.address);
                            } else {
                                errors.add(String.format(ErrorType.UNKNOWN_LABEL.getText(), op1.name,
                                entry.getKey() + 1));
                            }
                        }
                        break;
                    }
                    case NOT: {
                        code = (command.code << 1) + w;
                        addrByte = (command.mod << 6) + (command.reg << 3) + command.rm;
                        break;
                    }
                }
                String codeString = integersToCodeString(code);
                if (addrByte != 0) {
                    codeString = codeString.concat(integersToCodeString(addrByte));
                }
                if (data != null) {
                    codeString = codeString.concat(integersToCodeString(data));
                }
                machineCode.put(entry.getKey(), new CodeLine(cmdLine.address + org, codeString));
            }
        }
    }
    private String generateListing(String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<String> lines = parseSourceCode();
        int codeLength = lines.stream().mapToInt(String::length).max().orElse(0);
        stringBuilder.append("=====================================================");
        for (int i = 0; i <= codeLength; i++) {
            stringBuilder.append('=');
        }
        stringBuilder
        .append("\r\n")
        .append("[LINE] LOC: MACHINE CODE SOURCE\r\n")
        .append("=====================================================");
        for (int i = 0; i <= codeLength; i++) {
            stringBuilder.append('=');
        }
        stringBuilder.append("\r\n");
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            CodeLine codeLine = machineCode.get(i);
            stringBuilder.append(String.format("[%4d]", i + 1));
            if (codeLine == null) {
                stringBuilder.append(" : ");
            } else {
                stringBuilder
                .append(String.format(" %04x: ", codeLine.machineCodeIdx))
                .append(codeLine.line, 0, Math.min(12 * 3, codeLine.line.length()));
                for (int j = codeLine.line.length(); j < 12 * 3; j++) {
                    stringBuilder.append(' ');
                }
            }
            stringBuilder.append(" ").append(line).append("\r\n");
            if (codeLine != null && codeLine.line.length() > 12 * 3) {
                int j = 1;
                do {
                    stringBuilder
                    .append(" ")
                    .append(codeLine.line, j * 12 * 3, Math.min((j + 1) * 12 * 3, codeLine.line.length()))
                    .append("\r\n");
                    j++;
                } while (stringBuilder.length() < j * 12 * 3);
            }
        }
        return writeToFile(fileName, stringBuilder.toString());
    }
    private void generateObjectCodeFile(String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("H %-6s %012x %012x\n", segmentName, org, machineCodeLength));
        stringBuilder.append(String.format("T %012x %04x", org, machineCodeLength));
        for (Map.Entry<Integer, CodeLine> entry : machineCode.entrySet()) {
            stringBuilder.append(String.format("%s ", entry.getValue().line));
        }
        stringBuilder.append("\n");
        stringBuilder.append(String.format("E %012x", org));
        writeToFile(fileName, stringBuilder.toString());
    }
    private String generateListingWithErrors(String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<String> lines = parseSourceCode();
        int codeLength = lines.stream().mapToInt(String::length).max().orElse(0);
        stringBuilder.append("========");
        for (int i = 0; i <= codeLength; i++) {
            stringBuilder.append('=');
        }
        stringBuilder
        .append("\r\n")
        .append("[LINE] SOURCE\r\n")
        .append("========");
        for (int i = 0; i <= codeLength; i++) {
            stringBuilder.append('=');
        }
        stringBuilder.append("\r\n");
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            stringBuilder.append(String.format("[%4d]", i + 1)).append(" ").append(line).append("\r\n");
        }
        for (String error : errors) {
            stringBuilder.append(error).append("\r\n");
        }
        return writeToFile(fileName, stringBuilder.toString());
    }
    private String writeToFile(String fileName, String code) {
        String filePath = System.getProperty("user.dir").concat(File.separator.concat(fileName));
        Path path = Paths.get(filePath);
        try {
            Files.write(path, code.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return filePath;
    }
    private void analyzeSegmentLine(String segmentLine, int lineNum) {
        String[] words = segmentLine.split(" ");
        if (words[0].matches(NAME_PATTERN)) {
            if (!words[0].equalsIgnoreCase(Instruction.SEGMENT.name())) {
                segmentName = words[0];
            } else {
                errors.add(String.format(ErrorType.SEG_NAME_EXPECTED.getText(), lineNum + 1));
            }
        } else {
            errors.add(String.format(ErrorType.WRONG_SEG_NAME.getText(), words[0], lineNum + 1));
        }
    }
    private void analyzeOrgLine(String orgLine, int lineNum) {
        String[] words = orgLine.split(" ");
        if (words.length < 2) {
            errors.add(String.format(ErrorType.ORG_VALUE_EXPECTED.getText(), lineNum + 1));
        } else {
            if (isValueCorrect(words[1])) {
                org = stringToInteger(words[1]);
            } else {
            errors.add(String.format(ErrorType.WRONG_ORG_VALUE.getText(), words[1], lineNum + 1));
            }
        }
    }
    private void analyzeEndsLine(String endsLine, int lineNum) {
        String[] words = endsLine.split(" ");
        if (words[0].equalsIgnoreCase(segmentName)) {
            if (words[0].equalsIgnoreCase(Instruction.ENDS.name())) {
                errors.add(String.format(ErrorType.SEG_NAME_EXPECTED.getText(), lineNum + 1));
            }
        } else {
            errors.add(String.format(ErrorType.WRONG_SEG_NAME.getText(), words[0], lineNum + 1));
        }
    }
    private int analyzeVarLine(String varLine, int lineNum, int machineCodeLineNum) {
        String[] words = varLine.split(" ", 3);
        boolean isNamePresented = false;
        int valueLength = 0;
        if (words[0].matches(NAME_PATTERN)) {
            if (words[0].equalsIgnoreCase(Instruction.DW.name())) {
                errors.add(String.format(ErrorType.VAR_NAME_EXPECTED.getText(), lineNum + 1));
            }
            isNamePresented = true;
        } else {
            errors.add(String.format(ErrorType.WRONG_VAR_NAME.getText(), words[0], lineNum + 1));
        }
        if (words.length < 3) {
            if (isNamePresented) {
                errors.add(String.format(ErrorType.VAR_VALUE_EXPECTED.getText(), lineNum + 1));
            }
        } else {
            String[] values = words[2].split(",");
            boolean isValueCorrect = true;
            for (String value : values) {
                value = value.trim();
                if (!isValueCorrect(value)) {
                    errors.add(String.format(ErrorType.WRONG_VAR_VALUE.getText(), value, lineNum + 1));
                    isValueCorrect = false;
                }
            }
            if (isNamePresented && isValueCorrect) {
                Integer[] intValues = parseVarValues(values);
                varTable.add(new Variable(machineCodeLineNum, words[0], intValues));
                valueLength = values.length * 2;
                machineCode.put(lineNum, new CodeLine(machineCodeLineNum, integersToCodeString(intValues)));
            }
        }
        return valueLength;
    }
    private CommandLine analyzeCommandLine(String cmdLine, int lineNum, int machineCodeLineNum) {
        String cmd = cmdLine.substring(0, cmdLine.indexOf(" "));
        String opLine = cmdLine.substring(cmdLine.indexOf(" ")).trim();
        String[] operands = Arrays.stream(opLine.split(","))
        .map(String::trim)
        .collect(Collectors.toList())
        .toArray(new String[] {});
        CommandName cmdName = CommandName.valueOf(cmd);
        boolean isErrorsFound = false;
        Operand op2 = null;
        if (operands.length > 1) {
            try {
                op2 = stringToOperand(cmd, operands[1].trim(), null);
            } catch (InvalidParameterException e) {
                errors.add(String.format(ErrorType.UNKNOWN_OP_TYPE.getText(), operands[1].trim(), lineNum + 1));
                isErrorsFound = true;
            }
        }
        OperandType op2Type = op2 != null ? op2.type : null;
        Operand op1 = null;
        if (operands.length > 0) {
            try {
                op1 = stringToOperand(cmd, operands[0].trim(), op2Type);
            } catch (InvalidParameterException e) {
                errors.add(String.format(ErrorType.UNKNOWN_OP_TYPE.getText(), operands[0].trim(), lineNum + 1));
                isErrorsFound = true;
            }
        }
        OperandType op1Type = op1 != null ? op1.type : null;
        CommandKey key = new CommandKey(cmdName, op1Type, op2Type);
        Command command = commandTable.get(key);
        if (command != null) {
            switch (cmdName) {
                case TEST: case MOV: {
                    int reg1 = op1.reg.getCode();
                    if ((op1Type.equals(OperandType.REGISTER) && (op2Type.equals(OperandType.REGISTER) || op2Type.equals(OperandType.MEMORY)) || (op1Type.equals(OperandType.MEMORY) && op2Type.equals(OperandType.REGISTER)))) {
                        int reg2;
                        if (op1Type.equals(OperandType.MEMORY)) {
                            reg2 = op1.reg.equals(Register.BP) ? 6 : 7;
                            reg1 = op2.reg.getCode();
                        } else if (op2Type.equals(OperandType.MEMORY)) {
                            reg2 = op2.reg.equals(Register.BP) ? 6 : 7;
                        } else {
                            reg2 = op2.reg.getCode();
                        }
                        return new CommandLine(cmdName, new Command(command, reg1, reg2), machineCodeLineNum,
                        op1, op2);
                    }
                    if (op1Type.equals(OperandType.REGISTER) && op2Type.equals(OperandType.IMMEDIATE)) {
                        if (!isValueCorrect(op2.val.toString())) {
                            errors.add(String.format(ErrorType.TOO_BIG_NUM.getText(), op2.val, lineNum + 1));
                            return null;
                        }
                        if (cmdName.equals(CommandName.CMP)) {
                            return new CommandLine(cmdName, new Command(command, null, reg1), machineCodeLineNum, op1, op2);
                        } else {
                            return new CommandLine(cmdName, new Command(command, reg1, null), machineCodeLineNum, op1, op2);
                        }
                    }
                    break;
                }
                case JAE: {
                    return new CommandLine(cmdName, command, machineCodeLineNum, op1, null);
                }
                case INT: {
                    if (op1.val == 0x21) {
                    return new CommandLine(cmdName, command, machineCodeLineNum, op1, null);
                    }
                    errors.add(String.format(ErrorType.UNSUPPORTED_INT_VALUE.getText(), op1.val, lineNum + 1));
                    return null;
                }
                case DIV: {
                    return new CommandLine(cmdName, new Command(command, command.reg, op1.reg.getCode()), machineCodeLineNum, op1, null);
                }
            }
        }
        if (!isErrorsFound) {
            switch (cmdName) {
                case TEST: case MOV: {
                    if (op1 == null || op2 == null) {
                        errors.add(String.format(ErrorType.OP_EXPECTED.getText(), 2, lineNum + 1));
                        isErrorsFound = true;
                    }
                    break;
                }
                case JAE: case INT: case DIV: {
                    if (op1 == null) {
                        errors.add(cmdName.equals(CommandName.JS)
                        ? String.format(ErrorType.OP_EXPECTED.getText(), 1, lineNum + 1)
                        : String.format(ErrorType.LABEL_EXPECTED.getText(), lineNum + 1));
                        isErrorsFound = true;
                    } else {
                        if (cmdName.equals(CommandName.INT) && op1.val == null) {
                            errors.add(String.format(ErrorType.WRONG_INT_VALUE.getText(), operands[0], lineNum + 1));
                            isErrorsFound = true;
                        }
                    }
                    if (op2 != null) {
                        errors.add(String.format(ErrorType.WRONG_OP_NUM.getText(), lineNum + 1));
                        isErrorsFound = true;
                    }
                    break;
                }
            }
        }
        if (!isErrorsFound) {
            errors.add(String.format(ErrorType.WRONG_OP.getText(), lineNum + 1));
        }
        return null;
    }
    private String parseLineWithLabel(String line, int lineNum, int machineCodeLineNum) {
        int idxOfColon = line.indexOf(":");
        String label = line.substring(0, idxOfColon);
        if (label.matches(LABEL_PATTERN)) {
            try {
                labelTable.put(label, machineCodeLineNum);
            } catch (Exception e) {
                errors.add(String.format(ErrorType.NOT_UNIQUE_LABEL.getText(), label, lineNum + 1));
            }
        } else {
            errors.add(String.format(ErrorType.WRONG_LABEL_NAME.getText(), label, lineNum + 1));
        }
        return line.substring(idxOfColon + 1).trim();
    }
    private Integer[] parseVarValues(String[] values) {
        if (values == null || values.length == 0) {
            return new Integer[] {};
        }
        return Stream.of(values)
        .map(String::trim)
        .filter(this::isValueCorrect)
        .map(this::stringToInteger)
        .map(this::getReversedWord)
        .flatMap(Stream::of)
        .collect(Collectors.toList())
        .toArray(new Integer[] {});
    }
    private Integer stringToInteger(String s) {
        Integer res = null;
        if (s.matches(BIN_PATTERN)) {
            res = Integer.parseInt(StringUtils.chop(s), 2);
        } else if (s.matches(OCTAL_PATTERN)) {
            res = Integer.parseInt(StringUtils.chop(s), 8);
        } else if (s.matches(DEC_PATTERN)) {
            res = Integer.parseInt(s);
        } else if (s.matches(HEX_PATTERN)) {
            res = Integer.parseInt(StringUtils.chop(s), 16);
        }
        return res;
    }
    private Operand stringToOperand(String command, String value, OperandType secondOperandType) {
        Operand operand = null;
        if (command != null && value != null) {
            if (command.equalsIgnoreCase(CommandName.CMP.name()) && OperandType.IMMEDIATE.equals(secondOperandType)) {
                operand = new Operand(OperandType.REGISTER_AX, null, Register.AX, null);
            } else if (value.startsWith("[")) {
                operand = createMemoryOperand(value);
            } else if (Stream.of(Register.values()).map(Enum::name).anyMatch(value::equalsIgnoreCase)) {
                operand = new Operand(OperandType.REGISTER, null, Register.valueOf(value.toUpperCase()), null);
            } else if (isNumber(value)) {
                operand = new Operand(OperandType.IMMEDIATE, null, null, stringToInteger(value));
            } else if (command.equalsIgnoreCase(CommandName.JS.name())) {
                operand = new Operand(OperandType.LABEL, value, null, null);
            } else {
                throw new InvalidParameterException();
            }
        }
        return operand;
    }
    private String integersToCodeString(Integer... ints) {
        String res = "";
        for (Integer i : ints) {
            res = res.concat(String.format("%02x ", i & 0xFF));
        }
        return res;
    }
    private Operand createMemoryOperand(String value) {
        Operand operand = null;
        String[] memoryOperands = value
        .replaceAll("\\[", "")
        .replaceAll("]", "")
        .split("\\+");
        Register register = null;
        if (memoryOperands[0].trim().equalsIgnoreCase(Register.BP.name())) {
            register = Register.BP;
        } else if (memoryOperands[0].trim().equalsIgnoreCase(Register.BX.name())) {
            register = Register.BX;
        } else {
            throw new InvalidParameterException();
        }
        if (isValueCorrect(memoryOperands[1].trim())) {
            operand = new Operand(OperandType.MEMORY, null, register, stringToInteger(memoryOperands[1].trim()));
        } else {
            operand = new Operand(OperandType.MEMORY, memoryOperands[1].trim(), register, null);
        }
        return operand;
    }
    private boolean isNumber(String value) {
        return value.matches(BIN_PATTERN) || value.matches(OCTAL_PATTERN) || value.matches(DEC_PATTERN) || value.matches(HEX_PATTERN);
    }
    private boolean isValueCorrect(String value) {
        if (!isNumber(value)) {
            return false;
        }
        Integer intValue = stringToInteger(value);
        return ((intValue >= 0 && intValue < 65536));
    }
    private Integer[] getReversedWord(Integer word) {
        return new Integer[] {word & 0xFF, (word & 0xFF00) >> 8};
    }
    private class CodeLine {
        int machineCodeIdx;
        final String line;
        CodeLine(int machineCodeIdx, String line) {
            this.machineCodeIdx = machineCodeIdx;
            this.line = line;
        }
    }
    private class Variable {
        int address;
        final String name;
        final Integer[] values;
        Variable(int address, String name, Integer ...values) {
            this.address = address;
            this.name = name;
            this.values = values;
        }
    }
    private class Operand {
        final OperandType type;
        final String name;
        final Register reg;
        final Integer val;
        Operand(OperandType type, String name, Register reg, Integer val) {
            this.type = type;
            this.name = name;
            this.reg = reg;
            this.val = val;
        }
    }
    private class CommandLine {
        final CommandName commandName;
        final Command command;
        final int address;
        final Operand op1;
        final Operand op2;
        CommandLine(CommandName commandName, Command command, int address, Operand op1, Operand op2) {
            this.commandName = commandName;
            this.command = command;
            this.address = address;
            this.op1 = op1;
            this.op2 = op2;
        }
    }
    private class Command {
        final int code;
        final Integer d;
        final Integer mod;
        final Integer reg;
        final Integer rm;
        final Integer length;
        final Boolean w;
        Command(int code, Integer d, Integer mod, Integer reg, Integer rm, Boolean w, Integer length) {
            this.code = code;
            this.d = d;
            this.mod = mod;
            this.reg = reg;
            this.rm = rm;
            this.w = w;
            this.length = length;
        }
        Command(Command cmd, Integer reg, Integer rm) {
            this.code = cmd.code;
            this.d = cmd.d;
            this.mod = cmd.mod;
            this.reg = reg;
            this.rm = rm;
            this.w = cmd.w;
            this.length = cmd.length;
        }
    }
    private class CommandKey {
        final CommandName name;
        final OperandType op1;
        final OperandType op2;
        CommandKey(CommandName name, OperandType op1, OperandType op2) {
            this.name = name;
            this.op1 = op1;
            this.op2 = op2;
        }
        @Override
        public int hashCode() {
            return name.name().hashCode() + (op1 == null ? 0 : op1.hashCode()) * 2 + (op2 == null ? 0 : op2.hashCode()) * 10;
        }
        @Override
        public boolean equals(Object obj) {
            if (obj.getClass() == getClass()) {
                CommandKey cmd = (CommandKey) obj;
                if (name.name().equals(cmd.name.name())) {
                    if (op1 == null) {
                        return cmd.op1 == null;
                    }
                    if (op1.name().equals(cmd.op1.name())) {
                        if (op2 == null) {
                            return cmd.op2 == null;
                        }
                        return op2.name().equals(cmd.op2.name());
                    }
                }
                return false;
            }
            return false;
        }
    }
    private enum ErrorType {
        WRONG_CMD("Illegal instruction (%s) at %d"),
        WRONG_OP("Wrong operand type(s) at %d. Please, read documentation"),
        WRONG_OP_NUM("Too much operands at %d"),
        WRONG_ORG_VALUE("Wrong org value (%s) at %d"),
        WRONG_SEG_NAME("Wrong segment name (%s) at %d"),
        WRONG_VAR_NAME("Wrong variable name (%s) at %d"),
        WRONG_VAR_VALUE("Wrong variable value (%s) at %d"),
        WRONG_LABEL_NAME("Wrong label name (%s) at %d"),
        WRONG_INT_VALUE("Wrong int value (%s) at %d"),
        SEG_EXPECTED("SEGMENT expected at %d"),
        SEG_NAME_EXPECTED("Segment name expected at %d"),
        VAR_NAME_EXPECTED("Variable name expected at %d"),
        VAR_VALUE_EXPECTED("Variable value expected at %d"),
        ORG_EXPECTED("ORG expected at %d"),
        ORG_VALUE_EXPECTED("ORG value expected at %d"),
        ENDS_EXPECTED("ENDS expected at %d"),
        END_EXPECTED("END expected at %d"),
        LABEL_EXPECTED("Label name expected at %d"),
        OP_EXPECTED("%d operand(s) expected at %d"),
        TOO_BIG_NUM("Number (%s) is bigger than word at %d. Value must be like 0 <= x < 65536"),
        UNSUPPORTED_INT_VALUE("Unsupported INT value (%s) at %d"),
        NOT_UNIQUE_LABEL("Label with name (%s) at %d already exists"),
        UNKNOWN_OP_TYPE("Operand (%s) type was not recognized"),
        UNKNOWN_VAR("Unknown variable (%s) at %d"),
        UNKNOWN_LABEL("Unknown label (%s) at %d");
        private final String text;
        ErrorType(String text) {
            this .text = text;
        }
        public String getText() {
            return text;
        }
    }
    private enum Instruction {
        SEGMENT, ORG, DW, ENDS, END
    }
    private enum CommandName {
        MOV, TEST, DIV, JAE, INT
    }
    private enum OperandType {
        REGISTER, REGISTER_AX, MEMORY, IMMEDIATE, LABEL
    } 
    private enum Register{
        AX(0x0), CX(0x1), DX(0x2), BX(0x3),
        SP(0x4), BP(0x5), SI(0x6), DI(0x7);
        private final int code;
        Register (int code) {
            this .code = code;
        }
        public int getCode() {
            return code;
        }
    }
    public static void main (String[] args) {
        new AssemblerUI().setVisible(true);
    }
}


public class AssemblerUI extends javax.swing.JFrame {
    public AssemblerUI() {
        initComponents();
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        ResultDialog = new javax.swing.JDialog();
        OKButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        ResultText = new javax.swing.JTextArea();
        Start = new javax.swing.JButton();
        JScrollPanel = new javax.swing.JScrollPane();
        CodeContainer = new javax.swing.JTextArea();
        ResultDialog.setMinimumSize(new java.awt.Dimension(700, 230));
        OKButton.setText("OK");
        OKButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                OKButtonMouseClicked(evt);
            }
        });
        jScrollPane1.setEnabled(false);
        ResultText.setEditable(false);
        ResultText.setColumns(20);
        ResultText.setRows(5);
        ResultText.setBorder(null);
        jScrollPane1.setViewportView(ResultText);
        javax.swing.GroupLayout ResultDialogLayout = new javax.swing.GroupLayout(ResultDialog.getContentPane());
        ResultDialog.getContentPane().setLayout(ResultDialogLayout);
        ResultDialogLayout.setHorizontalGroup(
            ResultDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ResultDialogLayout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 499, Short.MAX_VALUE)
            .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ResultDialogLayout.createSequentialGroup()
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(OKButton)
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        ResultDialogLayout.setVerticalGroup(
            ResultDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ResultDialogLayout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 133,
            javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(OKButton)
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setName("AssemblerTranslator"); // NOI18N
        Start.setText("Start");
        Start.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                StartMouseClicked(evt);
            }
        });
        CodeContainer.setColumns(20);
        CodeContainer.setRows(5);
        JScrollPanel.setViewportView(CodeContainer);
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(JScrollPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 532, Short.MAX_VALUE)
            .addComponent(Start, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
            Short.MAX_VALUE))
            .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(JScrollPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE)
            .addGap(18, 18, 18)
            .addComponent(Start)
            .addContainerGap())
        );
        pack();
    }
    private void StartMouseClicked(java.awt.event.MouseEvent evt) {
        String result;
        if (CodeContainer.getText().isBlank()) {
            result = "Code container is empry. Please, write something";
        } else {
            result = new Assembler(CodeContainer.getText()).compile();
        }
        ResultText.setText(result);
        ResultDialog.setVisible(true);
    }
    private void OKButtonMouseClicked(java.awt.event.MouseEvent evt) {
        ResultDialog.setVisible(false);
    }
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AssemblerUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AssemblerUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AssemblerUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AssemblerUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AssemblerUI().setVisible(true);
            }  
        });
    }
    private javax.swing.JTextArea CodeContainer;
    private javax.swing.JScrollPane JScrollPanel;
    private javax.swing.JButton OKButton;
    private javax.swing.JDialog ResultDialog;
    private javax.swing.JTextArea ResultText;
    private javax.swing.JButton Start;
    private javax.swing.JScrollPane jScrollPane1;
}