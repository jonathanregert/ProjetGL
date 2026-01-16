.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 4
.limit locals 4
; Beginning of main (byte)
ldc 4
istore 1
ldc 2
istore 2
iload 1
iload 2
imul
ldc 3
isub
iload 2
ldc 1
iadd
if_icmpgt L0
goto L1
L0:
ldc 1
goto L2
L1:
ldc 0
L2:
istore 3
iload 3
ifne L3
goto L4
L3:
ldc 1
goto L5
L4:
ldc 0
L5:
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(I)V
; End of main (byte)
return
.end method
