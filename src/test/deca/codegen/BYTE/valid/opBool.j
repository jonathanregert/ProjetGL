.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 50
.limit locals 50
; Beginning of main (byte)
ldc 10
istore 1
ldc 3.14
fstore 2
ldc 1
istore 3
fload 2
fneg
iload 1
ldc 2
iadd
i2f
fmul
fstore 2
iload 1
ldc 0
if_icmplt L1
goto L3
L3:
iload 3
ifne L0
goto L4
L4:
fload 2
ldc 0.0
fcmpl
ifeq L0
goto L1
L0:
ldc "La logique est correcte."
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
goto L2
L1:
ldc "Condition fausse."
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
L2:
L5:
iload 1
ldc 0
if_icmpgt L8
goto L7
L8:
iload 3
ifne L6
goto L7
L6:
iload 1
ldc 1
isub
istore 1
iload 1
ldc 5
if_icmpeq L9
goto L10
L9:
ldc 0
istore 3
goto L11
L10:
L11:
goto L5
L7:
; End of main (byte)
return
.end method
