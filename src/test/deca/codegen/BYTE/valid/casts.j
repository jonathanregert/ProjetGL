.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 50
.limit locals 50
; Beginning of main (byte)
ldc 1
istore 1
ldc 2
istore 2
ldc 3
istore 3
ldc 4
istore 4
ldc 5
istore 5
ldc 1
istore 16
iload 1
i2f
fstore 11
iload 2
iload 3
iadd
i2f
fstore 12
iload 4
iload 5
imul
i2f
fstore 13
fload 11
ldc 1.0
fcmpl
ifne L0
goto L4
L4:
fload 12
ldc 5.0
fcmpl
ifne L0
goto L3
L3:
fload 13
ldc 20.0
fcmpl
ifne L0
goto L1
L0:
ldc 0
istore 16
goto L2
L1:
L2:
iload 1
i2f
fstore 14
iload 2
iload 3
iload 4
imul
iadd
i2f
fstore 15
fload 14
ldc 1.0
fcmpl
ifne L5
goto L8
L8:
fload 15
ldc 14.0
fcmpl
ifne L5
goto L6
L5:
ldc 0
istore 16
goto L7
L6:
L7:
ldc 3.7
f2i
istore 6
ldc 3.7
fneg
f2i
istore 7
fload 15
f2i
istore 8
iload 6
ldc 3
if_icmpne L9
goto L13
L13:
iload 7
ldc 3
ineg
if_icmpne L9
goto L12
L12:
iload 8
ldc 14
if_icmpne L9
goto L10
L9:
ldc 0
istore 16
goto L11
L10:
L11:
iload 1
iload 2
iadd
iload 3
iadd
i2f
ldc 2.5
fmul
f2i
istore 9
ldc 9.9
f2i
i2f
ldc 0.5
fadd
fstore 11
iload 9
ldc 15
if_icmpne L14
goto L17
L17:
fload 11
ldc 9.5
fcmpl
ifne L14
goto L15
L14:
ldc 0
istore 16
goto L16
L15:
L16:
iload 1
i2f
iload 2
i2f
ldc 2.0
fmul
fadd
iload 3
i2f
iload 4
i2f
fmul
fadd
iload 5
i2f
ldc 2.0
fdiv
fsub
ldc 7.9
f2i
i2f
fadd
ldc 3
i2f
f2i
i2f
fsub
fstore 12
fload 12
ldc 18.5
fcmpl
ifne L18
goto L19
L18:
ldc 0
istore 16
goto L20
L19:
L20:
fload 12
f2i
ldc 18
if_icmpne L21
goto L22
L21:
ldc 0
istore 16
goto L23
L22:
L23:
iload 6
iload 7
iadd
i2f
ldc 0.0
fcmpl
ifeq L25
goto L24
L24:
ldc 0
istore 16
goto L26
L25:
L26:
iload 5
i2f
ldc 3.0
fmul
f2i
dup
istore 9
istore 10
iload 9
ldc 15
if_icmpne L27
goto L30
L30:
iload 10
ldc 15
if_icmpne L27
goto L28
L27:
ldc 0
istore 16
goto L29
L28:
L29:
iload 10
i2f
ldc 3.0
fdiv
ldc 5.0
fcmpl
ifeq L32
goto L31
L31:
ldc 0
istore 16
goto L33
L32:
L33:
iload 16
ifne L34
goto L35
L34:
ldc "OK"
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
goto L36
L35:
ldc "ERROR"
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
L36:
; End of main (byte)
return
.end method
