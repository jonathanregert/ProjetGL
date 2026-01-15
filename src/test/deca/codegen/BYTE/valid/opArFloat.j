.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 50
.limit locals 50
; Beginning of main (byte)
ldc 0.5
fstore 1
ldc 5
i2f
fstore 2
fload 1
fload 2
fadd
fstore 3
; End of main (byte)
return
.end method
