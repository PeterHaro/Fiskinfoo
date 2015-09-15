<?xml version="1.0" encoding="ISO-8859-1"?>
<StyledLayerDescriptor version="1.0.0"
 xsi:schemaLocation="http://www.opengis.net/sld StyledLayerDescriptor.xsd"
 xmlns="http://www.opengis.net/sld"
 xmlns:ogc="http://www.opengis.net/ogc"
 xmlns:xlink="http://www.w3.org/1999/xlink"
 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <NamedLayer>
        <Name>Fishing Facilities</Name>
        <UserStyle>
            <FeatureTypeStyle>
              <Rule>
                    <Name>Long line (Point)</Name>
                    <Title>Long line (Point)</Title>
                    <ogc:Filter>
                        <ogc:And>
                        <ogc:PropertyIsEqualTo>
                          <ogc:Function name="geometryType">
                            <ogc:PropertyName>geom</ogc:PropertyName>
                          </ogc:Function>
                          <ogc:Literal>Point</ogc:Literal>
                        </ogc:PropertyIsEqualTo>
                           <ogc:PropertyIsEqualTo>
                          <ogc:PropertyName>tooltypecode</ogc:PropertyName>
                          <ogc:Literal>LONGLINE</ogc:Literal>
                          </ogc:PropertyIsEqualTo>
                        </ogc:And>
                    </ogc:Filter>
                    <PointSymbolizer>
                        <Graphic>
                            <Mark>
                                <WellKnownName>triangle</WellKnownName>
                                <Fill>
                                    <CssParameter name="fill">#DA0E0E</CssParameter>
                                </Fill>
                            </Mark>
                            <Size>12</Size>
                        </Graphic>
                    </PointSymbolizer>
                </Rule>
                <Rule>
                    <Name>Long line (LineString)</Name>
                    <Title>Long line (LineString)</Title>
                    <ogc:Filter>
                        <ogc:And>
                        <ogc:PropertyIsEqualTo>
                          <ogc:Function name="geometryType">
                            <ogc:PropertyName>geom</ogc:PropertyName>
                          </ogc:Function>
                          <ogc:Literal>LineString</ogc:Literal>
                        </ogc:PropertyIsEqualTo>
                           <ogc:PropertyIsEqualTo>
                          <ogc:PropertyName>tooltypecode</ogc:PropertyName>
                          <ogc:Literal>LONGLINE</ogc:Literal>
                          </ogc:PropertyIsEqualTo>
                        </ogc:And>
                    </ogc:Filter>
                    <LineSymbolizer>
                        <Stroke>
                            <CssParameter name="stroke">#DA0E0E</CssParameter>
                            <CssParameter name="stroke-width">2</CssParameter>
                            <CssParameter name="stroke-linecap">round</CssParameter>
                        </Stroke>
                    </LineSymbolizer>
                    <PointSymbolizer>
                        <Geometry>
                            <ogc:Function name="startPoint">
                                <ogc:PropertyName>geom</ogc:PropertyName>
                            </ogc:Function>
                        </Geometry>
                        <Graphic>
                            <Mark>
                                <WellKnownName>triangle</WellKnownName>
                                <Fill>
                                    <CssParameter name="fill">#DA0E0E</CssParameter>
                                </Fill>
                            </Mark>
                            <Size>12</Size>
                        </Graphic>
                    </PointSymbolizer>
                </Rule>
                  <Rule>
                    <Name>Nets (Point)</Name>
                    <Title>Nets (Point)</Title>
                    <ogc:Filter>
                        <ogc:And>
                        <ogc:PropertyIsEqualTo>
                          <ogc:Function name="geometryType">
                            <ogc:PropertyName>geom</ogc:PropertyName>
                          </ogc:Function>
                          <ogc:Literal>Point</ogc:Literal>
                        </ogc:PropertyIsEqualTo>
                           <ogc:PropertyIsEqualTo>
                          <ogc:PropertyName>tooltypecode</ogc:PropertyName>
                          <ogc:Literal>NETS</ogc:Literal>
                          </ogc:PropertyIsEqualTo>
                        </ogc:And>
                    </ogc:Filter>
                    <PointSymbolizer>
                        <Graphic>
                            <Mark>
                                <WellKnownName>triangle</WellKnownName>
                                <Fill>
                                    <CssParameter name="fill">#0874C1</CssParameter>
                                </Fill>
                            </Mark>
                            <Size>12</Size>
                        </Graphic>
                    </PointSymbolizer>
                </Rule>
                <Rule>
                    <Name>Nets (LineString)</Name>
                    <Title>Nets (LineString)</Title>
                    <ogc:Filter>
                        <ogc:And>
                        <ogc:PropertyIsEqualTo>
                          <ogc:Function name="geometryType">
                            <ogc:PropertyName>geom</ogc:PropertyName>
                          </ogc:Function>
                          <ogc:Literal>LineString</ogc:Literal>
                        </ogc:PropertyIsEqualTo>
                           <ogc:PropertyIsEqualTo>
                          <ogc:PropertyName>tooltypecode</ogc:PropertyName>
                          <ogc:Literal>NETS</ogc:Literal>
                          </ogc:PropertyIsEqualTo>
                        </ogc:And>
                    </ogc:Filter>
                    <LineSymbolizer>
                        <Stroke>
                            <CssParameter name="stroke">#0874C1</CssParameter>
                            <CssParameter name="stroke-width">2</CssParameter>
                            <CssParameter name="stroke-linecap">round</CssParameter>
                        </Stroke>
                    </LineSymbolizer>
                    <PointSymbolizer>
                        <Geometry>
                            <ogc:Function name="startPoint">
                                <ogc:PropertyName>geom</ogc:PropertyName>
                            </ogc:Function>
                        </Geometry>
                        <Graphic>
                            <Mark>
                                <WellKnownName>triangle</WellKnownName>
                                <Fill>
                                    <CssParameter name="fill">#0874C1</CssParameter>
                                </Fill>
                            </Mark>
                            <Size>12</Size>
                        </Graphic>
                    </PointSymbolizer>
                </Rule>
                  <Rule>
                    <Name>Crab pot (Point)</Name>
                    <Title>Crab pot (Point)</Title>
                    <ogc:Filter>
                        <ogc:And>
                        <ogc:PropertyIsEqualTo>
                          <ogc:Function name="geometryType">
                            <ogc:PropertyName>geom</ogc:PropertyName>
                          </ogc:Function>
                          <ogc:Literal>Point</ogc:Literal>
                        </ogc:PropertyIsEqualTo>
                           <ogc:PropertyIsEqualTo>
                          <ogc:PropertyName>tooltypecode</ogc:PropertyName>
                          <ogc:Literal>CRABPOT</ogc:Literal>
                          </ogc:PropertyIsEqualTo>
                        </ogc:And>
                    </ogc:Filter>
                    <PointSymbolizer>
                        <Graphic>
                            <Mark>
                                <WellKnownName>triangle</WellKnownName>
                                <Fill>
                                    <CssParameter name="fill">#EA5D00</CssParameter>
                                </Fill>
                            </Mark>
                            <Size>12</Size>
                        </Graphic>
                    </PointSymbolizer>
                </Rule>
                <Rule>
                    <Name>Crab pot (LineString)</Name>
                    <Title>Crab pot (LineString)</Title>
                    <ogc:Filter>
                        <ogc:And>
                        <ogc:PropertyIsEqualTo>
                          <ogc:Function name="geometryType">
                            <ogc:PropertyName>geom</ogc:PropertyName>
                          </ogc:Function>
                          <ogc:Literal>LineString</ogc:Literal>
                        </ogc:PropertyIsEqualTo>
                           <ogc:PropertyIsEqualTo>
                          <ogc:PropertyName>tooltypecode</ogc:PropertyName>
                          <ogc:Literal>CRABPOT</ogc:Literal>
                          </ogc:PropertyIsEqualTo>
                        </ogc:And>
                    </ogc:Filter>
                    <LineSymbolizer>
                        <Stroke>
                            <CssParameter name="stroke">#EA5D00</CssParameter>
                            <CssParameter name="stroke-width">2</CssParameter>
                            <CssParameter name="stroke-linecap">round</CssParameter>
                        </Stroke>
                    </LineSymbolizer>
                    <PointSymbolizer>
                        <Geometry>
                            <ogc:Function name="startPoint">
                                <ogc:PropertyName>geom</ogc:PropertyName>
                            </ogc:Function>
                        </Geometry>
                        <Graphic>
                            <Mark>
                                <WellKnownName>triangle</WellKnownName>
                                <Fill>
                                    <CssParameter name="fill">#EA5D00</CssParameter>
                                </Fill>
                            </Mark>
                            <Size>12</Size>
                        </Graphic>
                    </PointSymbolizer>
                </Rule>
                  <Rule>
                    <Name>Seismic (Point)</Name>
                    <Title>Seismic (Point)</Title>
                    <ogc:Filter>
                        <ogc:And>
                        <ogc:PropertyIsEqualTo>
                          <ogc:Function name="geometryType">
                            <ogc:PropertyName>geom</ogc:PropertyName>
                          </ogc:Function>
                          <ogc:Literal>Point</ogc:Literal>
                        </ogc:PropertyIsEqualTo>
                           <ogc:PropertyIsEqualTo>
                          <ogc:PropertyName>tooltypecode</ogc:PropertyName>
                          <ogc:Literal>SEISMIC</ogc:Literal>
                          </ogc:PropertyIsEqualTo>
                        </ogc:And>
                    </ogc:Filter>
                    <PointSymbolizer>
                        <Graphic>
                            <Mark>
                                <WellKnownName>triangle</WellKnownName>
                                <Fill>
                                    <CssParameter name="fill">#A06C49</CssParameter>
                                </Fill>
                            </Mark>
                            <Size>12</Size>
                        </Graphic>
                    </PointSymbolizer>
                </Rule>
                <Rule>
                    <Name>Seismic (LineString)</Name>
                    <Title>Seismic (LineString)</Title>
                    <ogc:Filter>
                        <ogc:And>
                        <ogc:PropertyIsEqualTo>
                          <ogc:Function name="geometryType">
                            <ogc:PropertyName>geom</ogc:PropertyName>
                          </ogc:Function>
                          <ogc:Literal>LineString</ogc:Literal>
                        </ogc:PropertyIsEqualTo>
                           <ogc:PropertyIsEqualTo>
                          <ogc:PropertyName>tooltypecode</ogc:PropertyName>
                          <ogc:Literal>SEISMIC</ogc:Literal>
                          </ogc:PropertyIsEqualTo>
                        </ogc:And>
                    </ogc:Filter>
                    <LineSymbolizer>
                        <Stroke>
                            <CssParameter name="stroke">#A06C49</CssParameter>
                            <CssParameter name="stroke-width">2</CssParameter>
                            <CssParameter name="stroke-linecap">round</CssParameter>
                        </Stroke>
                    </LineSymbolizer>
                    <PointSymbolizer>
                        <Geometry>
                            <ogc:Function name="startPoint">
                                <ogc:PropertyName>geom</ogc:PropertyName>
                            </ogc:Function>
                        </Geometry>
                        <Graphic>
                            <Mark>
                                <WellKnownName>triangle</WellKnownName>
                                <Fill>
                                    <CssParameter name="fill">#A06C49</CssParameter>
                                </Fill>
                            </Mark>
                            <Size>12</Size>
                        </Graphic>
                    </PointSymbolizer>
                </Rule>
                  <Rule>
                    <Name>Danish- / Purse- Seine (Point)</Name>
                    <Title>Danish- / Purse- Seine (Point)</Title>
                    <ogc:Filter>
                        <ogc:And>
                        <ogc:PropertyIsEqualTo>
                          <ogc:Function name="geometryType">
                            <ogc:PropertyName>geom</ogc:PropertyName>
                          </ogc:Function>
                          <ogc:Literal>Point</ogc:Literal>
                        </ogc:PropertyIsEqualTo>
                           <ogc:PropertyIsEqualTo>
                          <ogc:PropertyName>tooltypecode</ogc:PropertyName>
                          <ogc:Literal>DANPURSEINE</ogc:Literal>
                          </ogc:PropertyIsEqualTo>
                        </ogc:And>
                    </ogc:Filter>
                    <PointSymbolizer>
                        <Graphic>
                            <Mark>
                                <WellKnownName>triangle</WellKnownName>
                                <Fill>
                                    <CssParameter name="fill">#8100C1</CssParameter>
                                </Fill>
                            </Mark>
                            <Size>12</Size>
                        </Graphic>
                    </PointSymbolizer>
                </Rule>
                <Rule>
                    <Name>Danish- / Purse- Seine (LineString)</Name>
                    <Title>Danish- / Purse- Seine (LineString)</Title>
                    <ogc:Filter>
                        <ogc:And>
                        <ogc:PropertyIsEqualTo>
                          <ogc:Function name="geometryType">
                            <ogc:PropertyName>geom</ogc:PropertyName>
                          </ogc:Function>
                          <ogc:Literal>LineString</ogc:Literal>
                        </ogc:PropertyIsEqualTo>
                           <ogc:PropertyIsEqualTo>
                          <ogc:PropertyName>tooltypecode</ogc:PropertyName>
                          <ogc:Literal>DANPURSEINE</ogc:Literal>
                          </ogc:PropertyIsEqualTo>
                        </ogc:And>
                    </ogc:Filter>
                    <LineSymbolizer>
                        <Stroke>
                            <CssParameter name="stroke">#8100C1</CssParameter>
                            <CssParameter name="stroke-width">2</CssParameter>
                            <CssParameter name="stroke-linecap">round</CssParameter>
                        </Stroke>
                    </LineSymbolizer>
                    <PointSymbolizer>
                        <Geometry>
                            <ogc:Function name="startPoint">
                                <ogc:PropertyName>geom</ogc:PropertyName>
                            </ogc:Function>
                        </Geometry>
                        <Graphic>
                            <Mark>
                                <WellKnownName>triangle</WellKnownName>
                                <Fill>
                                    <CssParameter name="fill">#8100C1</CssParameter>
                                </Fill>
                            </Mark>
                            <Size>12</Size>
                        </Graphic>
                    </PointSymbolizer>
                </Rule>
                  <Rule>
                    <Name>Sensor / Cable (Point)</Name>
                    <Title>Sensor / Cable (Point)</Title>
                    <ogc:Filter>
                        <ogc:And>
                        <ogc:PropertyIsEqualTo>
                          <ogc:Function name="geometryType">
                            <ogc:PropertyName>geom</ogc:PropertyName>
                          </ogc:Function>
                          <ogc:Literal>Point</ogc:Literal>
                        </ogc:PropertyIsEqualTo>
                           <ogc:PropertyIsEqualTo>
                          <ogc:PropertyName>tooltypecode</ogc:PropertyName>
                          <ogc:Literal>SENSORCABLE</ogc:Literal>
                          </ogc:PropertyIsEqualTo>
                        </ogc:And>
                    </ogc:Filter>
                    <PointSymbolizer>
                        <Graphic>
                            <Mark>
                                <WellKnownName>triangle</WellKnownName>
                                <Fill>
                                    <CssParameter name="fill">#73A000</CssParameter>
                                </Fill>
                            </Mark>
                            <Size>12</Size>
                        </Graphic>
                    </PointSymbolizer>
                </Rule>
                <Rule>
                    <Name>Sensor / Cable (LineString)</Name>
                    <Title>Sensor / Cable (LineString)</Title>
                    <ogc:Filter>
                        <ogc:And>
                        <ogc:PropertyIsEqualTo>
                          <ogc:Function name="geometryType">
                            <ogc:PropertyName>geom</ogc:PropertyName>
                          </ogc:Function>
                          <ogc:Literal>LineString</ogc:Literal>
                        </ogc:PropertyIsEqualTo>
                           <ogc:PropertyIsEqualTo>
                          <ogc:PropertyName>tooltypecode</ogc:PropertyName>
                          <ogc:Literal>SENSORCABLE</ogc:Literal>
                          </ogc:PropertyIsEqualTo>
                        </ogc:And>
                    </ogc:Filter>
                    <LineSymbolizer>
                        <Stroke>
                            <CssParameter name="stroke">#73A000</CssParameter>
                            <CssParameter name="stroke-width">2</CssParameter>
                            <CssParameter name="stroke-linecap">round</CssParameter>
                        </Stroke>
                    </LineSymbolizer>
                    <PointSymbolizer>
                        <Geometry>
                            <ogc:Function name="startPoint">
                                <ogc:PropertyName>geom</ogc:PropertyName>
                            </ogc:Function>
                        </Geometry>
                        <Graphic>
                            <Mark>
                                <WellKnownName>triangle</WellKnownName>
                                <Fill>
                                    <CssParameter name="fill">#73A000</CssParameter>
                                </Fill>
                            </Mark>
                            <Size>12</Size>
                        </Graphic>
                    </PointSymbolizer>
                </Rule>
                  <Rule>
                    <Name>Mooring system (Point)</Name>
                    <Title>Mooring system (Point)</Title>
                    <ogc:Filter>
                        <ogc:And>
                        <ogc:PropertyIsEqualTo>
                          <ogc:Function name="geometryType">
                            <ogc:PropertyName>geom</ogc:PropertyName>
                          </ogc:Function>
                          <ogc:Literal>Point</ogc:Literal>
                        </ogc:PropertyIsEqualTo>
                           <ogc:PropertyIsEqualTo>
                          <ogc:PropertyName>tooltypecode</ogc:PropertyName>
                          <ogc:Literal>MOORING</ogc:Literal>
                          </ogc:PropertyIsEqualTo>
                        </ogc:And>
                    </ogc:Filter>
                    <PointSymbolizer>
                        <Graphic>
                            <Mark>
                                <WellKnownName>triangle</WellKnownName>
                                <Fill>
                                    <CssParameter name="fill">#FF42E5</CssParameter>
                                </Fill>
                            </Mark>
                            <Size>12</Size>
                        </Graphic>
                    </PointSymbolizer>
                </Rule>
                <Rule>
                    <Name>Mooring system (LineString)</Name>
                    <Title>Mooring system (LineString)</Title>
                    <ogc:Filter>
                        <ogc:And>
                        <ogc:PropertyIsEqualTo>
                          <ogc:Function name="geometryType">
                            <ogc:PropertyName>geom</ogc:PropertyName>
                          </ogc:Function>
                          <ogc:Literal>LineString</ogc:Literal>
                        </ogc:PropertyIsEqualTo>
                           <ogc:PropertyIsEqualTo>
                          <ogc:PropertyName>tooltypecode</ogc:PropertyName>
                          <ogc:Literal>MOORING</ogc:Literal>
                          </ogc:PropertyIsEqualTo>
                        </ogc:And>
                    </ogc:Filter>
                    <LineSymbolizer>
                        <Stroke>
                            <CssParameter name="stroke">#FF42E5</CssParameter>
                            <CssParameter name="stroke-width">2</CssParameter>
                            <CssParameter name="stroke-linecap">round</CssParameter>
                        </Stroke>
                    </LineSymbolizer>
                    <PointSymbolizer>
                        <Geometry>
                            <ogc:Function name="startPoint">
                                <ogc:PropertyName>geom</ogc:PropertyName>
                            </ogc:Function>
                        </Geometry>
                        <Graphic>
                            <Mark>
                                <WellKnownName>triangle</WellKnownName>
                                <Fill>
                                    <CssParameter name="fill">#FF42E5</CssParameter>
                                </Fill>
                            </Mark>
                            <Size>12</Size>
                        </Graphic>
                    </PointSymbolizer>
                </Rule>
                  <Rule>
                    <Name>Unknown (Point)</Name>
                    <Title>Unknown (Point)</Title>
                    <ogc:Filter>
                        <ogc:And>
                        <ogc:PropertyIsEqualTo>
                          <ogc:Function name="geometryType">
                            <ogc:PropertyName>geom</ogc:PropertyName>
                          </ogc:Function>
                          <ogc:Literal>Point</ogc:Literal>
                        </ogc:PropertyIsEqualTo>
                           <ogc:PropertyIsEqualTo>
                          <ogc:PropertyName>tooltypecode</ogc:PropertyName>
                          <ogc:Literal>UNK</ogc:Literal>
                          </ogc:PropertyIsEqualTo>
                        </ogc:And>
                    </ogc:Filter>
                    <PointSymbolizer>
                        <Graphic>
                            <Mark>
                                <WellKnownName>triangle</WellKnownName>
                                <Fill>
                                    <CssParameter name="fill">#000000</CssParameter>
                                </Fill>
                            </Mark>
                            <Size>12</Size>
                        </Graphic>
                    </PointSymbolizer>
                </Rule>
                <Rule>
                    <Name>Unknown (LineString)</Name>
                    <Title>Unknown (LineString)</Title>
                    <ogc:Filter>
                        <ogc:And>
                        <ogc:PropertyIsEqualTo>
                          <ogc:Function name="geometryType">
                            <ogc:PropertyName>geom</ogc:PropertyName>
                          </ogc:Function>
                          <ogc:Literal>LineString</ogc:Literal>
                        </ogc:PropertyIsEqualTo>
                           <ogc:PropertyIsEqualTo>
                          <ogc:PropertyName>tooltypecode</ogc:PropertyName>
                          <ogc:Literal>UNK</ogc:Literal>
                          </ogc:PropertyIsEqualTo>
                        </ogc:And>
                    </ogc:Filter>
                    <LineSymbolizer>
                        <Stroke>
                            <CssParameter name="stroke">#000000</CssParameter>
                            <CssParameter name="stroke-width">2</CssParameter>
                            <CssParameter name="stroke-linecap">round</CssParameter>
                        </Stroke>
                    </LineSymbolizer>
                    <PointSymbolizer>
                        <Geometry>
                            <ogc:Function name="startPoint">
                                <ogc:PropertyName>geom</ogc:PropertyName>
                            </ogc:Function>
                        </Geometry>
                        <Graphic>
                            <Mark>
                                <WellKnownName>triangle</WellKnownName>
                                <Fill>
                                    <CssParameter name="fill">#000000</CssParameter>
                                </Fill>
                            </Mark>
                            <Size>12</Size>
                        </Graphic>
                    </PointSymbolizer>
                </Rule>
                
            </FeatureTypeStyle>
        </UserStyle>
    </NamedLayer>
</StyledLayerDescriptor>
