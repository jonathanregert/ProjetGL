.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 4
.limit locals 8
; Beginning of main (byte)
ldc 3
istore 1
ldc 5
istore 2
ldc 7
istore 3
ldc 2
istore 4
iload 1
iload 2
iadd
i2f
fstore 5
iload 3
iload 4
isub
i2f
fstore 6
fload 5
fload 6
fmul
iload 1
iload 3
imul
i2f
fcmpl
ifgt L4
goto L3
L4:
iload 2
iload 4
iadd
iload 1
if_icmpeq L3
goto L0
L3:
fload 5
fload 6
fadd
iload 3
i2f
fcmpl
ifgt L5
goto L1
L5:
iload 1
iload 4
imul
iload 2
iload 3
iadd
if_icmple L0
goto L1
L0:
ldc 1
goto L2
L1:
ldc 0
L2:
istore 7
iload 7
ifne L6
goto L7
L6:
ldc 1
goto L8
L7:
ldc 0
L8:
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(I)V
; End of main (byte)
return
.end method
