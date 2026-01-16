.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 3
.limit locals 2
; Beginning of main (byte)
ldc 2
istore 1
iload 1
ldc 0
if_icmpne L3
goto L1
L3:
ldc 1
iload 1
idiv
ldc 0
if_icmpgt L0
goto L1
L0:
ldc 1
goto L2
L1:
ldc 0
L2:
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(I)V
; End of main (byte)
return
.end method
