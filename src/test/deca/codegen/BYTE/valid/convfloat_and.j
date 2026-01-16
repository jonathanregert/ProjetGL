.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 4
.limit locals 8
; Beginning of main (byte)
ldc 2
istore 1
ldc 3
istore 2
iload 1
iload 2
iadd
i2f
fstore 3
ldc 3
istore 4
ldc 4
istore 5
ldc 5
istore 6
iload 1
iload 2
iload 4
iload 5
isub
imul
iadd
istore 7
iload 7
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(I)V
fload 3
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(F)V
; End of main (byte)
return
.end method
