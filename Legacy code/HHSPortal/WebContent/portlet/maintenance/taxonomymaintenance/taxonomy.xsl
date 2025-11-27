<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="description" />
	<xsl:template match="element">
		<li>
			<xsl:attribute name="id">
				<xsl:value-of select="@id" />
			</xsl:attribute>
			<xsl:attribute name="title">
				<xsl:value-of select="@name" />
			</xsl:attribute>
			<xsl:attribute name="value">
				<xsl:value-of select="@name" />
			</xsl:attribute>
			<xsl:attribute name="type">
				<xsl:value-of select="@type" />
			</xsl:attribute>
			
			<span>
				<a href="javascript:void(0);" onclick="showList('{@id}','{@branchid}','{@type}')">
					<xsl:attribute name="id">
                 		<xsl:value-of select="@branchid" />
             		</xsl:attribute>
					<xsl:attribute name="type">
	                	<xsl:value-of select="@type" />
			        </xsl:attribute>
					<xsl:attribute name="name">
			        	<xsl:value-of select="@parentid" />
			        </xsl:attribute>
			
					<xsl:value-of select="@name" />
			
				</a>
			</span>
			
			<xsl:if test="element">
				<ul>
					<xsl:apply-templates />
				</ul>
			</xsl:if>
		</li>
	</xsl:template>
</xsl:stylesheet>

