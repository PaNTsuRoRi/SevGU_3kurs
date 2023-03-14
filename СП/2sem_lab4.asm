ORG 100H

mov bx, A[0] 
mov cx,N

k:
je exit

mov ax, A[si] 
cmp ax, bx

JBE next 
mov bx,ax 

next: 
add si,4 
loop k

exit:
mov A[0],bx 
 
INT 20H
N dw 10

A DD 8,5,25,90,84,-6,15,55,32,11
END START