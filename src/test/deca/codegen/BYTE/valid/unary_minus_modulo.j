.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 50
.limit locals 50
; Beginning of main (byte)
ldc 3
ineg
istore 1
ldc 10
ldc 3
irem
istore 2
iload 1
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(I)V
iload 2
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(I)V
; End of main (byte)
return
.end method
