.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 3
.limit locals 3
; Beginning of main (byte)
ldc 10
istore 1
ldc 0
istore 2
iload 1
iload 2
irem
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(I)V
; End of main (byte)
return
.end method
