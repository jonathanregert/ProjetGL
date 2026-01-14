.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 50
.limit locals 50
; Beginning of main (byte)
ldc 1
ifne L0
goto L1
L0:
goto L2
L1:
L2:
ldc 1
istore 1
iload 1
ifne L3
goto L4
L3:
goto L5
L4:
L5:
ldc 1
ifne L7
goto L6
L6:
goto L8
L7:
L8:
ldc 1
ifne L12
goto L10
L12:
ldc 1
ifne L9
goto L10
L9:
goto L11
L10:
L11:
ldc 0
ifne L13
goto L16
L16:
ldc 1
ifne L13
goto L14
L13:
goto L15
L14:
L15:
ldc 1
ldc 2
if_icmplt L17
goto L18
L17:
goto L19
L18:
L19:
ldc 1
ifne L20
goto L21
L20:
ldc 1
goto L22
L21:
ldc 0
L22:
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(I)V
ldc 1
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(I)V
ldc 1
ifne L23
goto L24
L23:
goto L25
L24:
L25:
; End of main (byte)
return
.end method
