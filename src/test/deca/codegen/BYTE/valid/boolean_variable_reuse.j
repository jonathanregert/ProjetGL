.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 3
.limit locals 3
; Beginning of main (byte)
ldc 3
istore 2
iload 2
ldc 0
if_icmpgt L0
goto L1
L0:
ldc 1
goto L2
L1:
ldc 0
L2:
istore 1
iload 1
ifne L3
goto L4
L3:
ldc 1
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(I)V
goto L5
L4:
ldc 0
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(I)V
L5:
; End of main (byte)
return
.end method
