.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 50
.limit locals 50
; Beginning of main (byte)
ldc 0
istore 1
L0:
iload 1
ldc 10
if_icmple L1
goto L2
L1:
iload 1
ldc 1
iadd
istore 1
goto L0
L2:
; End of main (byte)
return
.end method
