/*
 * Copyright 2003-2004, Franz-Josef Elmer, All rights reserved
 *
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details
 * (http://www.gnu.org/copyleft/lesser.html).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package jcckit.plot;

import jcckit.graphic.GraphicAttributes;
import jcckit.graphic.GraphicalElement;
import jcckit.graphic.GraphPoint;
import jcckit.graphic.Oval;
import jcckit.util.ConfigParameters;

/**
 *  A factory of circle symbols.
 *
 *  @author Franz-Josef Elmer
 */
public class CircleSymbolFactory extends AbstractSymbolFactory {
  /**
   * Creates an instance from the specified configuration parameters.
   * For the configuration parameters see the
   * <a href="AbstractSymbolFactory.html#AbstractSymbolFactory(jcckit.util.ConfigParameters)">
   * constructor</a> of the superclass {@link AbstractSymbolFactory}.
   */
  public CircleSymbolFactory(ConfigParameters config) {
    super(config);
  }

  /**
   * Creates a circle.
   * @param centerPosition Position of the center of the circle.
   * @param size Diameter of the circle.
   * @param attributes Circle attributes.
   */
  protected GraphicalElement createPlainSymbol(GraphPoint centerPosition,
                                               double size,
                                               GraphicAttributes attributes) {
    return new Oval(centerPosition, size, size, attributes);
  }
}
