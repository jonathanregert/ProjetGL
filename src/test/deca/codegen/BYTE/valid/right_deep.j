.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 6
.limit locals 6
; Beginning of main (byte)
ldc 10
istore 1
ldc 5
istore 2
ldc 0
istore 3
iload 1
iload 2
iadd
i2f
fstore 4
fload 4
ldc 12.0
fcmpl
ifgt L0
goto L3
L3:
iload 1
iload 2
ldc 2
ldc 3
ldc 4
ldc 5
iadd
imul
iadd
imul
iadd
ldc 0
if_icmpgt L4
goto L1
L4:
iload 1
iload 3
idiv
ldc 1
if_icmpgt L0
goto L1
L0:
ldc 1
goto L2
L1:
ldc 0
L2:
istore 5
iload 5
ifne L5
goto L6
L5:
ldc 1
goto L7
L6:
ldc 0
L7:
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(I)V
; End of main (byte)
return
.end method
