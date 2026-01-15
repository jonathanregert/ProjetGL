.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 50
.limit locals 50
; Beginning of main (byte)
ldc 1
istore 1
ldc 2
istore 2
ldc 3
istore 3
iload 1
iload 2
iload 3
iadd
iadd
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(I)V
; End of main (byte)
return
.end method
