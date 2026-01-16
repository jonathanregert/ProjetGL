.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 3
.limit locals 3
; Beginning of main (byte)
ldc 1
istore 1
ldc 2
istore 2
ldc 3
iload 1
imul
iload 2
ldc 2
idiv
isub
istore 1
; End of main (byte)
return
.end method
