.class public Main
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
.limit stack 2
.limit locals 3
; Beginning of main (byte)
ldc 3.14
fstore 1
ldc 0.0
fstore 2
fload 1
fload 2
fdiv
getstatic java/lang/System/out Ljava/io/PrintStream;
swap
invokevirtual java/io/PrintStream/print(F)V
; End of main (byte)
return
.end method
