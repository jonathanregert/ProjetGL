.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 50
.limit locals 50
; Beginning of main (byte)
ldc 1
istore 1
ldc 2
istore 2
ldc 3
istore 3
iload 1
iload 2
iadd
iload 3
if_icmpgt L3
goto L1
L3:
iload 3
iload 2
isub
iload 1
if_icmpeq L0
goto L1
L0:
ldc 1
goto L2
L1:
ldc 0
L2:
istore 4
iload 4
ifne L4
goto L5
L4:
ldc 1
goto L6
L5:
ldc 0
L6:
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(I)V
; End of main (byte)
return
.end method
