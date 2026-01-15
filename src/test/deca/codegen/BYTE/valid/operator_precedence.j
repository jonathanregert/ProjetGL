.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 50
.limit locals 50
; Beginning of main (byte)
ldc 1
ldc 2
ldc 3
imul
iadd
ldc 4
ldc 2
idiv
isub
istore 1
iload 1
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(I)V
; End of main (byte)
return
.end method
