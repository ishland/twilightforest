package twilightforest.enums;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum WebShape implements StringRepresentable {
	SHORT,
	TALL,
	NONE;

	@Override
	public String getSerializedName() {
		return this.name().toLowerCase(Locale.ROOT);
	}
}
