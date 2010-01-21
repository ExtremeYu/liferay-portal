<%
/**
 * Copyright (c) 2000-2009 Liferay, Inc. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
%>

<%@ include file="/html/portlet/directory/init.jsp" %>

<%
themeDisplay.setIncludeServiceJs(true);

OrganizationSearch searchContainer = (OrganizationSearch)request.getAttribute("liferay-ui:search:searchContainer");

OrganizationDisplayTerms displayTerms = (OrganizationDisplayTerms)searchContainer.getDisplayTerms();

String type = displayTerms.getType();
%>

<liferay-ui:search-toggle
	id="toggle_id_directory_organization_search"
	displayTerms="<%= displayTerms %>"
	buttonLabel="search"
>
	<aui:fieldset>
		<aui:column>
			<aui:input name="<%= displayTerms.NAME %>" size="20" type="text" value="<%= displayTerms.getName() %>" />

			<aui:select name="<%= displayTerms.TYPE %>">
				<aui:option label="any" selected="<%= (displayTerms.getType() == null) %>" />

				<%
				for (String curType : PropsValues.ORGANIZATIONS_TYPES) {
				%>

					<aui:option label="<%= curType %>" selected="<%= type.equals(curType) %>" />

				<%
				}
				%>
			</aui:select>
		</aui:column>

		<aui:column>
			<aui:input name="<%= displayTerms.STREET %>" size="20" type="text" value="<%= displayTerms.getStreet() %>" />

			<aui:select label="country" name="<%= displayTerms.COUNTRY_ID %>" />
		</aui:column>

		<aui:column>
			<aui:input name="<%= displayTerms.CITY %>" size="20" type="text" value="<%= displayTerms.getCity() %>" />

			<aui:select label="region" name="<%= displayTerms.REGION_ID %>" />
		</aui:column>

		<aui:column>
			<aui:input name="<%= displayTerms.ZIP %>" size="20" type="text" value="<%= displayTerms.getZip() %>" />
		</aui:column>
	</aui:fieldset>
</liferay-ui:search-toggle>

<%
Organization organization = null;

if (displayTerms.getParentOrganizationId() > 0) {
	try {
		organization = OrganizationLocalServiceUtil.getOrganization(displayTerms.getParentOrganizationId());
	}
	catch (NoSuchOrganizationException nsoe) {
	}
}
%>

<c:if test="<%= organization != null %>">
	<aui:input name="<%= UserDisplayTerms.ORGANIZATION_ID %>" type="hidden" value="<%= organization.getOrganizationId() %>" />

	<br />

	<liferay-ui:message key="filter-by-organization" />: <%= HtmlUtil.escape(organization.getName()) %><br />
</c:if>

<c:if test="<%= windowState.equals(WindowState.MAXIMIZED) %>">
	<aui:script>
		Liferay.Util.focusFormField(document.<portlet:namespace />fm.<portlet:namespace /><%= displayTerms.NAME %>);
		Liferay.Util.focusFormField(document.<portlet:namespace />fm.<portlet:namespace /><%= displayTerms.KEYWORDS %>);
	</aui:script>
</c:if>

<aui:script use="liferay-dynamic-select">
	new Liferay.DynamicSelect(
		[
			{
				select: '<portlet:namespace /><%= displayTerms.COUNTRY_ID %>',
				selectId: 'countryId',
				selectDesc: 'name',
				selectVal: '<%= displayTerms.getCountryId() %>',
				selectData: Liferay.Address.getCountries
			},
			{
				select: '<portlet:namespace /><%= displayTerms.REGION_ID %>',
				selectId: 'regionId',
				selectDesc: 'name',
				selectVal: '<%= displayTerms.getRegionId() %>',
				selectData: Liferay.Address.getRegions
			}
		]
	);
</aui:script>