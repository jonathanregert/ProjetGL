.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 50
.limit locals 50
; Beginning of main (byte)
ldc 5
istore 1
iload 1
i2f
ldc 1.5
fsub
fstore 2
iload 1
ldc 15
iadd
istore 1
fload 2
iload 1
i2f
fadd
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(F)V
; End of main (byte)
return
.end method
