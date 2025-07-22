package ch.njol.skript.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.minecart.PoweredMinecart;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Powered Minecart Fuel Ticks")
@Description("Get or set the fuel ticks of a powered minecart. Supports Timespan and integer ticks.")
@Examples({
	"set fuel ticks of {_cart} to 1 second",
	"add 20 ticks to fuel ticks of {_cart}"
})
@Since("TBD ¯\\_(ツ)_/¯")
public class ExprFuelTicks extends SimplePropertyExpression<Minecart, Integer> {

	static {
		register(ExprFuelTicks.class, Integer.class, "fuel ticks", "entities");
	}

	@Override
	public Integer convert(Minecart minecart) {
		if (!(minecart instanceof PoweredMinecart))
			return null;
		return ((PoweredMinecart) minecart).getFuel();
	}

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		return super.init(exprs, matchedPattern, isDelayed, parseResult);
	}


	@Override
	public Class<? extends Integer> getReturnType() {
		return Integer.class;
	}

	@Override
	protected String getPropertyName() {
		return "fuel ticks";
	}

	@Override
	public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
		if (mode == ChangeMode.ADD || mode == ChangeMode.REMOVE || mode == ChangeMode.SET || mode == ChangeMode.RESET || mode == ChangeMode.DELETE)
			return CollectionUtils.array(Integer.class, Timespan.class);
		return null;
	}

	@Override
	public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
		if (delta == null)
			return;

		Object value = delta[0];
		int changeValue;

		if (value instanceof Integer) {
			changeValue = (Integer) value;
		} else if (value instanceof Timespan) {
			changeValue = (int) ((Timespan) value).getAs(Timespan.TimePeriod.TICK);
		} else {
			return;
		}

		Entity[] entities = getExpr().getArray(event);
		for (Entity entity : entities) {
			if (!(entity instanceof PoweredMinecart))
				continue;

			PoweredMinecart powered = (PoweredMinecart) entity;
			int currentFuel = powered.getFuel();

			switch (mode) {
				case ADD:
					powered.setFuel(currentFuel + changeValue);
					break;
				case REMOVE:
					powered.setFuel(currentFuel - changeValue);
					break;
				case SET:
					powered.setFuel(changeValue);
					break;
				case RESET:
				case DELETE:
					powered.setFuel(0);
					break;
				default:
					break;
			}
		}
	}
}