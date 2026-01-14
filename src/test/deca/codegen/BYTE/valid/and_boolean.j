.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 50
.limit locals 50
; Beginning of main (byte)
ldc 0
ifne L3
goto L1
L3:
ldc 1
ldc 0
idiv
ldc 0
if_icmpeq L0
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
