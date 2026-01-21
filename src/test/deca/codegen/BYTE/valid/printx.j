.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 2
.limit locals 3
; Beginning of main (byte)
ldc 3.5
fstore 1
ldc 5
istore 2
ldc "Normal: "
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
fload 1
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(F)V
ldc "Hex: "
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
fload 1
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(F)V
ldc 1.2
fstore 1
ldc "Retour au normal: "
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
fload 1
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/println(F)V
; End of main (byte)
return
.end method
