.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 4
.limit locals 7
; Beginning of main (byte)
ldc 30
istore 1
ldc 4
istore 2
ldc 6
istore 3
ldc 2
istore 4
iload 1
iload 2
iload 4
iadd
idiv
i2f
fstore 6
fload 6
iload 3
ineg
iload 2
irem
i2f
fadd
iload 3
iload 4
isub
i2f
fadd
fstore 5
fload 5
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(F)V
; End of main (byte)
return
.end method
