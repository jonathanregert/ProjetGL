.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 50
.limit locals 50
; Beginning of main (byte)
ldc 3
istore 1
ldc 2
istore 2
iload 1
iload 2
iadd
i2f
fstore 3
fload 3
ldc 2
i2f
fmul
fstore 3
fload 3
ldc 5
i2f
fdiv
fstore 3
fload 3
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(F)V
; End of main (byte)
return
.end method
