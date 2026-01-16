.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 2
.limit locals 4
; Beginning of main (byte)
ldc 5
istore 1
ldc 7
istore 2
iload 1
iload 2
iadd
istore 3
iload 3
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(I)V
; End of main (byte)
return
.end method
