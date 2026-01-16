.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 7
.limit locals 9
; Beginning of main (byte)
ldc 3
istore 1
ldc 2
istore 2
ldc 7
istore 3
ldc 1
istore 4
ldc 9
istore 5
ldc 12
istore 6
ldc 0
istore 7
iload 1
ldc 3
iload 3
iload 4
iload 5
iload 6
iload 7
iadd
irem
iadd
idiv
imul
iadd
istore 8
; End of main (byte)
return
.end method
