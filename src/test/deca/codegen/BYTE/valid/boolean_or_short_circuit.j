.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 50
.limit locals 50
; Beginning of main (byte)
ldc 0
istore 1
ldc 1
ifne L0
goto L3
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
istore 2
iload 2
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
