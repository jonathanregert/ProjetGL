.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 2
.limit locals 2
; Beginning of main (byte)
ldc 1
istore 1
iload 1
ldc 2
iadd
i2f
ldc 0.0
fcmpl
ifeq L0
goto L1
L0:
ldc "ok!"
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
goto L2
L1:
ldc "1"
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
L2:
; End of main (byte)
return
.end method
