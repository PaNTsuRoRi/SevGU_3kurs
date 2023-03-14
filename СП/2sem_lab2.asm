LAB2 SEGMENT
    ASSUME CS:LAB2,DS:LAB2
    ORG 100H
START:
    ;Вычисление X^3
    XOR AX,AX    
    MOV AL,X
    MUL X        ;AX <- X*X 
    MOV BL,X
    MUL BX       ;DX,AX <- X*X*X
    MOV RESULT,AX
    MOV RESULT+2,DX
    ;Вычисление B+G
    MOV AL,B
    XOR AH,AH
    ADD AL,G
    ADC AH,0
    ;Вычисление MIN(A,B+G)
    MOV BL,A
    XOR BH,BH 
    CMP AX,BX
    JBE RES
    MOV AL,A
    XOR AH,AH 
RES:  ;Вычисление X^3-MIN(A,B+G)  
    SUB RESULT,AX
    SBB RESULT+2,0
    JO ER
    JMP EX
ER: MOV ERROR,0FFH
 
EX: INT 20H

X   DB  60H  
A   DB  80H
B   DB  90H
G   DB  90H

RESULT DD ?
ERROR DB ?
    
LAB2 ENDS
    END START