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
ldc 4
istore 4
ldc 5
istore 5
ldc 6
istore 6
ldc 7
istore 7
ldc 8
istore 8
iload 1
iload 2
iadd
iload 3
iadd
iload 4
iadd
iload 5
iadd
iload 6
iadd
iload 7
iadd
iload 8
iadd
istore 9
iload 9
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(I)V
; End of main (byte)
return
.end method
