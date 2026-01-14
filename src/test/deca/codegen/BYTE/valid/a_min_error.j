.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 50
.limit locals 50
; Beginning of main (byte)
ldc 3
istore 1
ldc 7
istore 2
ldc 5
istore 3
ldc 0
istore 4
ldc 0
istore 6
ldc 2.5
fstore 8
ldc 1
istore 11
ldc 0
istore 12
L0:
fload 8
ldc 5.0
fcmpl
iflt L1
goto L2
L1:
fload 8
ldc 1.0
fadd
fstore 8
goto L0
L2:
; End of main (byte)
return
.end method
