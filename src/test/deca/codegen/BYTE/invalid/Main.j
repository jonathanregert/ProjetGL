.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 2
.limit locals 2
; Beginning of main (byte)
ldc 2147483647
istore 1
iload 1
ldc 1
iadd
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(I)V
; End of main (byte)
return
.end method
