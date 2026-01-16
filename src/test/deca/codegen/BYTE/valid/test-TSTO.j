.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 4
.limit locals 4
; Beginning of main (byte)
ldc 1
istore 1
ldc 2
istore 2
iload 1
iload 2
iload 1
iload 2
iadd
imul
iadd
istore 3
; End of main (byte)
return
.end method
