find ./ -name "output*" -print0|xargs -0 -t rm -rf
find ./ -name "*.elf" -print0|xargs -0 -t rm -rf
echo 完成