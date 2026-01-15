.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 50
.limit locals 50
; Beginning of main (byte)
ldc 20
istore 1
ldc 4
istore 2
ldc 3
istore 3
ldc 2
istore 4
ldc 1
istore 5
iload 1
iload 2
isub
i2f
fstore 6
fload 6
iload 3
i2f
fsub
iload 4
i2f
fcmpl
ifgt L5
goto L1
L5:
iload 2
iload 3
iadd
iload 1
if_icmplt L4
goto L1
L4:
iload 1
iload 4
irem
ldc 0
if_icmpeq L1
goto L3
L3:
iload 3
iload 4
iadd
iload 5
iadd
iload 1
if_icmpgt L0
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
