.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 2
.limit locals 1
; Beginning of main (byte)
ldc 1
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(I)V
ldc 2
ldc 3
iadd
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(I)V
ldc 1
ifne L0
goto L1
L0:
ldc 4
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(I)V
goto L2
L1:
ldc 5
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(I)V
L2:
L3:
ldc 1
ifne L4
goto L5
L4:
goto L3
L5:
; End of main (byte)
return
.end method
