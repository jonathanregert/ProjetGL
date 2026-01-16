.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 3
.limit locals 3
; Beginning of main (byte)
ldc 10
istore 1
ldc 3.14
fstore 2
fload 2
fneg
iload 1
ldc 2
iadd
i2f
fmul
fstore 2
fload 2
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(F)V
; End of main (byte)
return
.end method
