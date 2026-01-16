.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 4
.limit locals 4
; Beginning of main (byte)
ldc 3
istore 1
ldc 5
istore 2
iload 1
iload 2
iadd
ldc 10
if_icmpgt L1
goto L3
L3:
iload 1
iload 2
imul
ldc 20
if_icmplt L1
goto L0
L0:
ldc 1
goto L2
L1:
ldc 0
L2:
istore 3
iload 3
ifne L4
goto L5
L4:
ldc 1
goto L6
L5:
ldc 0
L6:
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(I)V
; End of main (byte)
return
.end method
