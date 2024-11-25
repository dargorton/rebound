package net.rebound.client.devutil;

import net.minecraft.nbt.NbtCompound;

public interface ISerializable<T> {
    NbtCompound toTag();

    T fromTag(NbtCompound tag);
}