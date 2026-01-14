.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 50
.limit locals 50
; Beginning of main (byte)
ldc 2
istore 1
ldc 3
istore 3
ldc 1
istore 5
ldc 3
istore 6
iload 1
iload 3
iadd
istore 2
ldc 0
istore 4
L0:
iload 6
ldc 0
if_icmpgt L1
goto L2
L1:
iload 6
ldc 1
iadd
istore 7
L3:
iload 7
ldc 0
if_icmpgt L4
goto L5
L4:
iload 6
iload 7
iadd
iload 1
ldc 2
imul
if_icmpgt L6
goto L7
L6:
iload 4
iload 6
iload 7
imul
iadd
iload 1
iload 3
iadd
isub
istore 4
goto L8
L7:
iload 4
iload 7
iload 6
isub
isub
istore 4
L8:
iload 4
iload 6
iadd
iload 7
iload 1
imul
isub
istore 8
iload 8
ldc 0
if_icmplt L9
goto L10
L9:
iload 5
ifne L15
goto L13
L15:
ldc 1
ifne L12
goto L13
L12:
ldc 1
goto L14
L13:
ldc 0
L14:
istore 5
goto L11
L10:
iload 5
ifne L19
goto L17
L19:
iload 8
ldc 0
if_icmpne L16
goto L17
L16:
ldc 1
goto L18
L17:
ldc 0
L18:
istore 5
L11:
iload 7
ldc 1
isub
istore 7
goto L3
L5:
iload 6
ldc 1
isub
istore 6
goto L0
L2:
ldc "z="
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
iload 4
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(I)V
ldc "go="
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
iload 5
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
invokevirtual java/io/PrintStream/println(I)V
; End of main (byte)
return
.end method
