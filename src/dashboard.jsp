<%@ include file="/html/include/header.jsp"%>
<portlet:renderURL var="applicationLocationURL">
	<portlet:param name="mvcPath" value="/html/view.jsp" />
	<portlet:param name="applicationName" value="DOCCONTROL" />
</portlet:renderURL>
<portlet:actionURL var="actionURL" />

<%!
public String callFunction(Node viewSubCategory, int level, String viewListOutput, String viewTitle, Map<String, PortletLink> viewNevigationTreeMap)
{
	level++;

	Iterator<Node> viewSubCategoryItr = viewSubCategory.getChildren();
	Iterator<Node> viewSubCategoryListDeterminationItr = viewSubCategory.getChildren();

	////DebugUtility.debug("Adding 1:"+viewSubCategory);
	boolean viewSubCategoryCreated = false;

	if(viewSubCategoryListDeterminationItr != null && viewSubCategoryListDeterminationItr.hasNext())
    {
		Node viewSubSubCategory = viewSubCategoryListDeterminationItr.next();
		String rootclasss = null;
     	String iClass = "";
     	String href= null;
    	String navitemClass =  "nav-item";


		if(!viewSubSubCategory.toString().trim().equals("") && !viewSubSubCategory.toString().trim().equals("null"))
    	{
	    	iClass ="data-jstree='{ \"type\" : \"default\" }'";

	    	href = "href=\"javascript:;\"";

		   	if(!viewSubCategory.toString().trim().equals("") && !viewSubCategory.toString().trim().equals("null"))
	    	{

				if(!viewSubCategoryCreated)
	   			{
	   				viewListOutput += "<li "+iClass+">";
					viewListOutput += "<a "+href+" >"+viewSubCategory +"</a>";
	       			viewListOutput += "<ul>";


	           	}


	       		viewSubCategoryCreated = true;
	    	}
    	}

    }


	while(viewSubCategoryItr != null && viewSubCategoryItr.hasNext())
    {
    	Node viewSubSubCategory = viewSubCategoryItr.next();
    	//String rootclasss = null;
     	String iClass = null;
     	String href= null;
		String viewTitleHREF = null;


    	if(!viewSubSubCategory.toString().trim().equals("") && !viewSubSubCategory.toString().trim().equals("null"))
    	{
    		iClass ="data-jstree='{ \"type\" : \"default\" }'";
	    	viewTitleHREF = viewTitle + viewSubSubCategory.toString().trim();
			viewListOutput = callFunction(viewSubSubCategory,level, viewListOutput,viewTitleHREF,viewNevigationTreeMap);

	    	href = "href=\"javascript:;\"";

	    }
    	else if(!viewSubCategory.toString().trim().equals("") && !viewSubCategory.toString().trim().equals("null"))
    	{
    		iClass ="data-jstree='{ \"type\" : \"file\" }'";
    		//viewTitle += viewSubCategory.toString().trim();
    		////DebugUtility.debug("Adding Dashboard View Title:"+viewTitle);

         	href = " href='"+viewNevigationTreeMap.get(viewTitle)+"'";


    		////DebugUtility.debug("viewSubCategory:"+viewSubCategory);
    		viewListOutput += "<li "+iClass+">";

    		viewListOutput += "<a "+href+" >"+viewSubCategory;

    		if(!viewSubSubCategory.toString().trim().equals("") && !viewSubSubCategory.toString().trim().equals("null"))
        	{

        	}
    		else
    		{
   				viewListOutput += "</a>";
    		}
    		viewListOutput += "</li>";
    	}




    }

	if(viewSubCategoryCreated)
		{
			viewListOutput += "</ul>";
		}	viewListOutput += "</li>";

	return viewListOutput;

}

%>

<%

    //Variables to store all links for the dashboard page
    ArrayList<PortletLink> favouriteApplicationLinks = null;
    ArrayList<PortletLink> favouriteProcessLinks = null;
    ArrayList<PortletLink> locationLinks = null;
    PortletLink homePageLink = null;
    ArrayList<PortletLink> feedLinks = null;
    ArrayList<PortletLink> menuLinks = null;
    ArrayList<PortletLink> allApplicationLinks = null;
    ArrayList<PortletLink> allReportLinks = null;
    ArrayList<PortletLink> favReportLinks = null;
    ArrayList<PortletLink> pendingTasksLinks = null;
    ArrayList<PortletLink> openDocumentLinks = null;
    ArrayList<PortletLink> taskPortalLinks = null;
    ArrayList<PortletLink> taskDocumentPortalLinks = null;
    HashMap<String, HashMap<String,String>> announcements = null;

    PortletLink reportingApplicationLink = null;
    PortletLink settingsApplicationLink = null;
    PortletLink userProfileLink = null;

    LinkedHashMap<String, HashMap<String,String>> dashboardReportData = (LinkedHashMap)renderRequest.getAttribute("getDashboardReportData");


    if(renderRequest.getAttribute("pageLinks") != null)
    {
        //PageLinks shall contain all the links for dashboard (all linkes were set in the control handler)
        PageLinks pageLinks = (PageLinks)renderRequest.getAttribute("pageLinks");

        //The PortletLinks below can be used in the JSPs to make the content dynamic
        //Get Tasks Links
        taskLinks = pageLinks.getTaskLinks();
        //Get Favourite Application Links
        favouriteApplicationLinks = pageLinks.getFavouriteApplicationLinks();

        //Get Fav Processes
        favouriteProcessLinks = pageLinks.getFavouriteProcessLinks();

        //Get Location Links
        locationLinks = pageLinks.getLocationLinks();

          //Get Task Portal Links
        taskPortalLinks = pageLinks.getTaskPortalLinks();

        //Get Feed Links
        feedLinks = pageLinks.getFeedLinks();
        //Get Menu links
        menuLinks = pageLinks.getMenuLinks();

        taskDocumentPortalLinks = pageLinks.getTaskDocumentLinks();
      //  String pagePath = renderRequest.getParameter("mvcPath").toString()

      //  out.println("pagePath "+pagePath);
        //get home page links
        homePageLink = pageLinks.getHomePageLink();

        announcements = pageLinks.getAnnouncements();
       	//Get Report Links (COMMENTED OUT, not required for now)
        //favReportLinks = pageLinks.getFavouriteReportLinks();

    }

    Map<String,PortletLink> menuTreeMap = new HashMap<String,PortletLink>();
      if(menuLinks !=null){
            for(int i=0;i<menuLinks.size();i++){
                //Display the link and any parameters in the link (Title, Image Source etc)
                PortletLink menuLink = menuLinks.get(i);
                if(null != menuLink.getParameter("VAR_MENU_ROOT_DISPLAY_NAME") && "Home".equals(menuLink.getParameter("VAR_MENU_ROOT_DISPLAY_NAME"))){
                     menuTreeMap.put(menuLink.getParameter("VAR_MENU_ROOT_DISPLAY_NAME"), homePageLink);
                }else{

                	//DebugUtility.debug("menuLink :"+i+":"+menuLink);

                    if(!("null".equals(menuLink.getParameter("VAR_MENU_CHILD_TITLE").toString().trim()) || "".equals(menuLink.getParameter("VAR_MENU_CHILD_TITLE").toString().trim()))){
                    	//DebugUtility.debug("1 menuLink:"+i+":"+menuLink.getParameter("VAR_MENU_CHILD_TITLE").toString());

                        menuTreeMap.put(menuLink.getParameter("VAR_MENU_CHILD_TITLE"),menuLink);
                        menuTreeMap.put(menuLink.getParameter("VAR_REPORT_NAME_FULL"),menuLink);

                        //DebugUtility.debug("VAR_REPORT_NAME_FULL:"+menuLink.getParameter("VAR_REPORT_NAME_FULL"));

                    }else if(!("null".equals(menuLink.getParameter("VAR_MENU_CHILD_DISPLAY_NAME1").toString().trim()) || "".equals(menuLink.getParameter("VAR_MENU_CHILD_DISPLAY_NAME1").toString().trim()))){
                    	//DebugUtility.debug("2 menuLink:"+i+":"+menuLink.getParameter("VAR_MENU_CHILD_DISPLAY_NAME1").toString());

                    	menuTreeMap.put(menuLink.getParameter("VAR_MENU_CHILD_DISPLAY_NAME1"),menuLink);
                    }else if(!("null".equals(menuLink.getParameter("VAR_MENU_CHILD_DISPLAY_NAME").toString().trim()) || "".equals(menuLink.getParameter("VAR_MENU_CHILD_DISPLAY_NAME").toString().trim()))){

                    	//DebugUtility.debug("3 menuLink:"+i+":"+menuLink.getParameter("VAR_MENU_CHILD_DISPLAY_NAME").toString());
                    	menuTreeMap.put(menuLink.getParameter("VAR_MENU_CHILD_DISPLAY_NAME"),menuLink);

                    }else if(!("null".equals(menuLink.getParameter("VAR_MENU_ROOT_DISPLAY_NAME").toString().trim()) || "".equals(menuLink.getParameter("VAR_MENU_ROOT_DISPLAY_NAME").toString().trim()))){

                    	//DebugUtility.debug("4 menuLink:"+i+":"+menuLink.getParameter("VAR_MENU_ROOT_DISPLAY_NAME").toString());
                    	menuTreeMap.put(menuLink.getParameter("VAR_MENU_ROOT_DISPLAY_NAME"),menuLink);

                    }

                }
    	    }
        } %>
<!-- BEGIN MAIN MENU -->
<!-- DOC: Apply "hor-menu-light" class after the "hor-menu" class below to have a horizontal menu with white background -->
<!-- DOC: Remove data-hover="dropdown" and data-close-others="true" attributes below to disable the dropdown opening on mouse hover -->
<div class="hor-menu hor-menu-light">

	<ul class="nav navbar-nav">
		<%
                      	Map<Integer , Node> menuNodeMap = (Map<Integer , Node>)renderRequest.getAttribute("menuData");

						String fullReportName = "";

						if(menuNodeMap != null)
                      	for(int k=0;k<menuNodeMap.size();k++){
                      	    Node menuChildNode = menuNodeMap.get(k);
                            //Node menuChildNode = menuChildren.next();
                            boolean isAnalytics = false;

                            if(menuChildNode != null)
                            {
                            	Iterator<Node> menuChildren_2 = menuChildNode.getChildren();
                            	boolean nodeValid = ("".equals(menuChildNode.getChildren().next().toString().trim()) || "null".equals(menuChildNode.getChildren().next().toString().trim()));

                            	if(menuChildNode.toString().trim().equals("Analytics"))
                            	{
                            		isAnalytics = true;
                            	}

                            	//DebugUtility.debug("menuChildNode:"+menuChildNode.toString());
                            	////DebugUtility.debug("5 menuLink:"+menuChildNode.getChildren().next().toString().trim());

                            String classs = "menu-dropdown classic-menu-dropdown"; String other_class ="data-hover='megamenu-dropdown' data-close-others='true' data-toggle='dropdown'"; String iClass ="fa fa-angle-down";
                            if(("".equals(menuChildNode.getChildren().next().toString().trim()) || "null".equals(menuChildNode.getChildren().next().toString().trim()))){
                                classs =  "menu-dropdown";
                                other_class = "";
                                iClass = "fa";
                            }


                            String rootHref = " href='javascript:;'";
                            //create links for root if need
                            if(nodeValid){
                                rootHref = " href='"+menuTreeMap.get(menuChildNode.toString().trim())+"'";
                            }

                            %>
		<li class="<%= classs %>"><a <%= other_class %> <%= rootHref %>>
				<%= menuChildNode %> <i class="<%= iClass %>"></i>
		</a> <%
                      		if(!nodeValid){

                            	if(menuChildren_2 != null){ %>

			<ul class="dropdown-menu pull-left firstLevelMenuQMS">
               
                <% if(isAnalytics) {%>
                    <li>
                          <a id="PowerBI-link"><i title="PowerBI"
                        class="fa fa-bar-chart"></i> PowerBI</a>
                    </li>

                <%  }%>
                
                 <form   id="form-analytics" style="display: none;"  target="_blank" method="POST">
                            <input type="text" style="display: none;" id="username" name="username" value="<%=  user.getEmailAddress() %>"  />
                            <input type="text" style="display: none;" id="password" name="password" value="<%= JSESSIONID %>"/>
                            <button id="PowerBI-button">PowerBI</button>
                </form>
                 <script>
                    document.getElementById("form-analytics").action = 'http://' + window.location.hostname +":5300/";
                    document.getElementById("PowerBI-link").addEventListener("click",function(){
                        document.getElementById("PowerBI-button").click();
                    })

                </script>
				<% while(menuChildren_2.hasNext())
                                        {

                                            Node menuChildNode_2 = menuChildren_2.next();
                                            boolean node2Valid = ("".equals(menuChildNode_2.getChildren().next().toString().trim()) || "null".equals(menuChildNode_2.getChildren().next().toString().trim()));
                                            String root2Href = " href=''";

                                            fullReportName = "";

                                            fullReportName = menuChildNode_2.toString().replace(" ", "");

                                            //DebugUtility.debug("menuChildNode_2:"+menuChildNode_2.toString());

                                            ////DebugUtility.debug("6 menuLink:"+menuChildNode_2.getChildren().next().toString().trim());

                                            //create links for root if need
                                            if(node2Valid){
                                                root2Href = " href='"+menuTreeMap.get(menuChildNode_2.toString().trim())+"'";
                                            }

											if(!node2Valid && isUserRestricted.trim().equals("false") || (!node2Valid && isUserRestricted.trim().equals("true") &&  (
													menuChildNode_2.toString().trim().equals("Document Control") ))){

												%>

				<li class=" dropdown-submenu"><a <% if(root2Href.trim().equals("href=''")){ out.print("");}else{out.print(root2Href);} %>> <i
						class="icon-briefcase"></i> <%= menuChildNode_2 %></a> <%

											Iterator<Node> menuChildren_3 = menuChildNode_2.getChildren();
												if(menuChildren_3 != null){

													int topPositionCount = 0;

													while(menuChildren_3.hasNext())
													{
														menuChildren_3.next();
														topPositionCount++;
													}
													menuChildren_3 = menuChildNode_2.getChildren();

													if(menuChildren_3 != null)
													{
														Node menuChildNode_3 = menuChildren_3.next();

														//DebugUtility.debug("menuChildNode_3:"+menuChildNode_3.toString());

														if(menuChildNode_3 != null && menuChildNode_3.toString().trim().equals("DISABLED")) {

															Iterator<Node> menuChildren_4 = menuChildNode_3.getChildren();
												            if(menuChildren_4 != null){

												            	topPositionCount = 0;
												            	while(menuChildren_4.hasNext())
												            	{
												            		menuChildren_4.next();
												            		topPositionCount++;
												            	}

												            }
														}
													}

													menuChildren_3 = menuChildNode_2.getChildren();

													if(isAnalytics)
													{
														topPositionCount = -1;
													}
												%>

					<ul class="dropdown-menu"
						style="top:-<%= topPositionCount*35 %>px; left: 50%; <% if(menuChildNode_2.getChildrenList().size() > 8){out.print("overflow-y: scroll; height:200px;");} %>">

						<% while(menuChildren_3.hasNext())
                                        			{

                                            			Node menuChildNode_3 = menuChildren_3.next();

                                            			boolean node3Valid = ("".equals(menuChildNode_3.getChildren().next().toString().trim()) || "null".equals(menuChildNode_3.getChildren().next().toString().trim()));

                                            			////DebugUtility.debug("7 menuLink:"+menuChildNode_3.getChildren().next().toString().trim());

                                            			String root3Href = " href=''";
                                                        //create links for root if need
                                                        if(node3Valid){
                                                            root3Href = " href='"+menuTreeMap.get(menuChildNode_3.toString().trim())+"'";
                                                        }
                                                        String classs1 = "class=''";
                                                        if(!node3Valid){
                                                            classs1= "class='dropdown-submenu'";
                                                        }

                                                        //DebugUtility.debug("menuTreeMap menuChildNode_3:"+menuChildNode_3.toString());

                                            	    %>
						<%--  //******2nd level disabled if no menu name
                                            	    --%>
						<% if(!menuChildNode_3.toString().trim().equals("DISABLED")) {



						%>
						<li <%= classs1 %>><a <% if(root2Href.trim().equals("href=''")){ out.print("");}else{out.print(root2Href);} %>> <%= menuChildNode_3 %></a>
							<%}
						else
						{

						}
							%> <%
                                        		        if(!node3Valid){
                                        		    	Iterator<Node> menuChildren_4 = menuChildNode_3.getChildren();
											            if(menuChildren_4 != null){

											            	int topNode_3PositionCount = 0;
											            	while(menuChildren_4.hasNext())
											            	{
											            		menuChildren_4.next();
											            		topNode_3PositionCount++;
											            	}
											            	menuChildren_4 = menuChildNode_3.getChildren();

											            %> <%--  //******2nd level disabled if no menu name--%>
							<% if(!menuChildNode_3.toString().trim().equals("DISABLED")){ %>
							<ul class="dropdown-menu" style="top:-<%= topNode_3PositionCount*35 %>px; left: 50%;">
								<%}%>



								<% while(menuChildren_4.hasNext())
                                                                	{
                                                                	Node menuChildNode_4 = menuChildren_4.next();

                                                                	////DebugUtility.debug("8 menuLink:"+menuChildNode_4.toString().trim());

                                                                	//DebugUtility.debug("menuChildNode_4:"+menuChildNode_4.toString());

                                                                	String lastLevelReportName = fullReportName + menuChildNode_4.toString().replace(" ", "");

                                                                	//DebugUtility.debug("lastLevelReportName:"+lastLevelReportName);

                                                                	String root4Href = " href='"+menuTreeMap.get(lastLevelReportName)+"'";

                                                                	//DebugUtility.debug("root4Href 1:"+root4Href);

                                                                	if(menuTreeMap.get(lastLevelReportName) == null)
                                                                	{
                                                                		root4Href = " href='"+menuTreeMap.get(menuChildNode_4.toString().trim())+"'";

                                                                		//DebugUtility.debug("root4Href 2:"+root4Href);

                                                                	}

                                                                	%>

								<li class=""><a <% if(root4Href.trim().equals("href=''")){ out.print("");}else{out.print(root4Href);} %>><i
										class="fa fa-rocket"></i> <%= menuChildNode_4 %></a></li>

								<%

                                } %>


								<%--  //******2nd level disabled --%>
								<% if(!menuChildNode_3.toString().trim().equals("DISABLED")){ %>
							</ul> <%}%> <% } } %> <%--  //******2nd level disabled --%> <% if(!menuChildNode_3.toString().trim().equals("DISABLED")){ %>
						</li>
						<%}%>

						<%

                         } %>
					</ul> <% } } %></li>
				<% } %>
			</ul> <% } } %></li>

		<%} } %>
	</ul>
</div>
<!-- END MEGA MENU -->
</div>
</div>
<!-- END HEADER MENU -->
</div>
<!-- END HEADER -->
<%
/*out.print("<br>");
out.print("SESSION INFORMATION: ");
out.print(renderRequest.getPortletSession().getId());

HttpSession sessionFromSessionManager = (HttpSession)SessionManager.instance().getSession(renderRequest.getPortletSession().getId());

out.print("<br>");
out.print(sessionFromSessionManager.getId());*/


 %>
<% // out.println(actionURL.toString());%>
<%  //out.println(applicationLocationURL.toString());
%>
<!-- BEGIN PAGE CONTAINER -->
<!-- BEGIN PAGE HEAD -->
<div class="page-head">
	<div class="container">
		<!-- BEGIN PAGE TITLE -->
		<div class="page-title">
			<h1 class="step-1"></h1>
			Dashboard <small>start new tasks or monitor existing tasks
				from here</small>
			</h1>

		</div>
		<!-- END PAGE TITLE -->

	</div>
</div>
<!-- END PAGE HEAD -->
<!-- BEGIN PAGE CONTENT -->
<a id="tour" style="display: none;"></a>
<div class="page-content">
	<div class="container">
		<div class="page-content-inner">

		<%

		%>
		<!-- BEGIN PAGE CONTENT INNER -->
		<!-- BEGIN PAGE CONTENT INNER -->

		<!-- <div class="page-content-inner"> -->
		<!-- BEGIN PAGE CONTENT INNER -->

		<% if(changeLocationSelection && locationLinks != null && locationLinks.size() > 0)
			{
		%>
					<div class="portfolio-content portfolio-3">
						<div class="clearfix">
                                    <div id="js-filters-lightbox-gallery1" class="cbp-l-filters-dropdown cbp-l-filters-dropdown-floated">
                                        <div class="cbp-l-filters-dropdownWrap border-grey-salsa">
                                        <%
                                        	if(userCurrentLocation != null && !userCurrentLocation.equals(""))
                                        	{

                                        %>
                                        		<div class="cbp-l-filters-dropdownHeader uppercase"><%= locationValuesMap.get(userCurrentLocation).get("SYS_LOCATION_NAME") %></div>

                                        <%
                                        	}
                                        	else
                                        	{
                                        %>
                                        		<div class="cbp-l-filters-dropdownHeader uppercase">Change your Location</div>
                                        <%
                                        	}
                                        %>

                                            <div class="cbp-l-filters-dropdownList">

                                                <%
                                                //Set<String> locationKeys = locationValuesMap.keySet();

                                                for(PortletLink locationLink:locationLinks)
                                                {
                                                	//HashMap<String,String> locationValues = locationValuesMap.get(locationKey);
                                                %>
	                                                <a href="<%= locationLink %>" >
	                                                	<div class="cbp-filter-item uppercase"> <%= locationLink.getParameter("SYS_LOCATION_NAME") %> </div>
	                                                </a>

                                                <%
                                                }
                                                %>

                                            </div>
                                        </div>
                                    </div>
                                    <!--
                                    <div id="js-filters-lightbox-gallery2" class="cbp-l-filters-button cbp-l-filters-left">
                                        <div data-filter="*" class="cbp-filter-item-active cbp-filter-item btn blue btn-outline uppercase">All</div>
                                        <div data-filter=".graphic" class="cbp-filter-item btn blue btn-outline uppercase">Graphic</div>
                                        <div data-filter=".logos" class="cbp-filter-item btn blue btn-outline uppercase">Logo</div>
                                        <div data-filter=".motion" class="cbp-filter-item btn blue btn-outline uppercase">Motion</div>
                                    </div>
                                     -->
                                </div>
                                <div id="js-grid-lightbox-gallery" class="cbp">
		<%
			}
		%>

		<% if(openLocationSelection && locationLinks != null && locationLinks.size() > 0) // (Open Location If Block)
			{

                                 //Set<String> locationKeys = locationValuesMap.keySet();
                                 for(PortletLink locationLink:locationLinks)
                                 {
                                 	//HashMap<String,String> locationValues = locationValuesMap.get(locationKey);
                                 	//DebugUtility.debug("locationLink:"+locationLink);
                                 	String imageURL = !locationLink.getParameter("SYS_LOCATION_URL").toString().equals("")?locationLink.getParameter("SYS_LOCATION_URL"):"/theme-resources/assets/admin/layout/img/projects/default.jpg";
                                 	String locationDescription = !locationLink.getParameter("SYS_LOCATION_DESC").toString().equals("")?locationLink.getParameter("SYS_LOCATION_DESC"):"";

                                 	if(!locationLink.getParameter("SYS_LOCATION_ID").toString().equals("-1"))
                                 	{
                                 %>



                                    <div class="cbp-item web-design graphic print motion">
                                        <a href="<%= locationLink %>" class="cbp-caption">
                                            <div class="cbp-caption-defaultWrap">
                                                <img src="<%= imageURL %>" alt="" > </div>
                                            <div class="cbp-caption-activeWrap">
                                                <div class="cbp-l-caption-alignLeft">
                                                    <div class="cbp-l-caption-body">
                                                        <div class="cbp-l-caption-title"><%= locationLink.getParameter("SYS_LOCATION_NAME") %></div>
                                                        <div class="cbp-l-caption-desc"><%= locationDescription %></div>
                                                    </div>
                                                </div>
                                            </div>
                                        </a>
                                    </div>

                        		<%
                                 	}
                                 }
                        		%>

                                <!--
                                <div id="js-loadMore-lightbox-gallery" class="cbp-l-loadMore-button">
                                    <a href="/theme-resources/assets/global/plugins/cubeportfolio/ajax/loadMore3.html" class="cbp-l-loadMore-link btn grey-mint btn-outline" rel="nofollow">
                                        <span class="cbp-l-loadMore-defaultText">LOAD MORE</span>
                                        <span class="cbp-l-loadMore-loadingText">LOADING...</span>
                                        <span class="cbp-l-loadMore-noMoreLoading">NO MORE WORKS</span>
                                    </a>
                                </div>
                                 -->
                                 </div>
                            </div>
                        </div>
        	<%

        	}

		else //disaplay the dashboard (Open Location If Block)
		{
		%>

			<% if(changeLocationSelection && locationLinks != null && locationLinks.size() > 0)
				{
			%>
					<!-- Start Close Location Block Code -->
			 			</div>
			 				</div>
			 					</div>

            		<!-- End Close Location Block Code -->
            <%
				}
				else
				{
				%>
					</div>
				<%
				}
	            %>
         <!-- END PAGE CONTENT INNER -->
		<!-- END PAGE CONTENT INNER -->
		<div class="row">
		<%
		Set<String> reportViewNames = dashboardReportData.keySet();
		for(String reportViewName:reportViewNames)
		{
			HashMap<String,String> reportMap = dashboardReportData.get(reportViewName);

		%>

		<div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
                                    <div class="dashboard-stat2 ">
                                        <div class="display">
                                        	<%
                                        		boolean openListOnClick = false;
                                        		String listURL = null;
                                        		//Check if a list is to be opened on click
                                        		if(reportMap.get("openListReq") != null)
                                        		{
                                        			if(reportMap.get("openListReq").toString().equals("true"))
                                        			{
                                        				if(reportMap.get("openListID") != null
                                        						&& reportMap.get("openListName") != null
                                        						&& !reportMap.get("openListID").toString().equals(""))
                                        				{
	                                        				listURL = reportMap.get("openListURL").toString();

                                        				}


                                        			}
                                        		}


                                        	%>

                                        	<%
                                        	if(listURL != null)
                                    		{
                                        	%>
                                        	<a href="<%= listURL %>">
                                        	<%
                                    		}
                                        	%>

                                        	<div class="number">
                                                <h3 class="font-<%= reportMap.get("class") %>">
                                                <%
                                                	double doubleValue = new Double(reportMap.get("actualValue")).doubleValue();
													DecimalFormat df = new DecimalFormat("#.##");
													doubleValue = Double.valueOf(df.format(doubleValue));
												%>
                                                    <span data-counter="counterup" data-value="<%= doubleValue %>">0 </span>
                                                    <small class="font-<%= reportMap.get("class") %>"><%= reportMap.get("targetUnit") %></small>
                                                </h3>
                                                <small><%= reportMap.get("displayName") %></small>
                                            </div>
                                            <div class="icon">
                                                <i class="<%= reportMap.get("icon") %>"></i>
                                            </div>

                                            <%
                                        	if(listURL != null)
                                    		{
                                        	%>
                                        	</a>
                                        	<%
                                    		}
                                        	%>
                                        </div>
                                        <div class="progress-info">
                                            <% if(reportMap.get("targetReq") != null &&
                                            		reportMap.get("targetReq").toString().equals("true"))
                                            	{
                                            	Double percentageDouble = ((Double.parseDouble(reportMap.get("actualValue")))/(Double.parseDouble(reportMap.get("targetValue")))) * 100;
                                            	int percentage = percentageDouble.intValue();
                                            %>

                                            <div class="progress">
                                                <span style="width: <%= percentage %>%;" class="progress-bar progress-bar-success <%= reportMap.get("class") %>">
                                                    <span class="sr-only"><%= percentage %>% <%= reportMap.get("targetName") %></span>
                                                </span>
                                            </div>
                                            <div class="status">
                                                <div class="status-title"> <%= reportMap.get("targetName") %> </div>
                                                <%
                                                	if(reportMap.get("targetValue") != null &&
                                            		!reportMap.get("targetValue").toString().equals(""))
                                            		{
                                                %>
                                                <div class="status-number"><%= percentage %>%  </div>
                                                <%
                                            		}
                                            	%>
                                            </div>
                                            <%
                                            	}
                                            %>
                                        </div>
                                    </div>
                                </div>

		<%

		}
		%>

                      </div>
		<div class="row  margin-top-10">
			<div class="col-md-6 col-sm-12">
				<!-- BEGIN PORTLET-->
				<div class="portlet light tasks-widget">
					<div class="portlet-title">
						<div class="caption caption-md">
							<i class="icon-bar-chart theme-font hide"></i> <span id="step-2"
								class="caption-subject theme-font bold uppercase">TASKS</span> <span
								class="caption-helper"><%= taskLinks.size() %> pending</span>
						</div>
						<div class="actions">
						<div class="icon">
						<a href="<%=taskPageLink %>" id="task-portal-button">
							Task Portal <!--<i class="btn btn-circle btn-icon-only icon-rocket"></i>-->
						</a>

                        </div>

						</div>
					</div>
					<!-- for loop goes here -->
					<br>
					<div class="portlet-body">
						<% Map<String, PortletLink> TaskLinksTreeMap = new HashMap<String,PortletLink>();
						  if(taskLinks !=null){
        				        for(int i=0;i<taskLinks.size();i++){
        				            //Display the link and any parameters in the link (Title, Image Source etc)
        				            PortletLink newLink = taskLinks.get(i);
        			                TaskLinksTreeMap.put(newLink.getParameter("VAR_TASK_TITLE")+"-"+newLink.getParameter("VAR_TASK_ID"),newLink);
            				    }
        				    }
						%>



						<div id="tree_1" class="tree-demo">
							<ul>
								<% Tree tree = (Tree)renderRequest.getAttribute("taskData");
	                        	//get tree data
	                        	Iterator<Node> children = tree.getRoot().getChildren();

	                          	while(children.hasNext())
	                            {
	                                Node childNode = children.next();
	                                //////DebugUtility.debug("FIRST LEVEL:"+childNode);
	                            %>
								<li><%= childNode %>
									 <% Iterator<Node> children_2 = childNode.getChildren();
                                    	if(children_2 != null){ %>
									<ul>
										<% while(children_2.hasNext())
                                                {
                                                    Node childNode_2 = children_2.next();
                                                    //////DebugUtility.debug("SECOND LEVEL:"+childNode_2);

                                            		%>
										<li data-jstree='{ "opened" : true }'><%= childNode_2 %>
											<% Iterator<Node> children_3 = childNode_2.getChildren();
														if(children_3 != null){ %>
											<ul>
												<% while(children_3.hasNext())
                                                			{
                                                    			Node childNode_3 = children_3.next();
                                                    			//////DebugUtility.debug("THIRD LEVEL:"+childNode_3);
                                                    			if(childNode_3 != null && TaskLinksTreeMap.get(childNode_3.toString().trim()) != null)
                                                    			{
                                                    				String encodedTaskString = null;
                                                    				if (TaskLinksTreeMap.get(childNode_3.toString().trim()).getParameter("VAR_TASK_TITLE") != null) {

                                                    					encodedTaskString = TaskLinksTreeMap.get(childNode_3.toString().trim()).getParameter("VAR_TASK_TITLE");

                                                    					//encodedTaskString = new String (TaskLinksTreeMap.get(childNode_3.toString().trim()).getParameter("VAR_TASK_TITLE").getBytes("iso-8859-1"), "UTF-8");
                                                    				}
                                                  %>
												<li data-jstree='{ "type" : "file" }'><a
													href="<%= TaskLinksTreeMap.get(childNode_3.toString().trim()) %>"><%=
													encodedTaskString
													%></a></li>


												<%
                                                    			}

                                                    		}

												%>
											</ul> <% } %></li>
										<% } %>
									</ul> <% } %></li>
								<% } %>
							</ul>

						</div>
					</div>

				</div>
				<!-- END PORTLET-->
			</div>
			<div class="col-md-6 col-sm-12">
				<!-- BEGIN PORTLET-->
				<div class="portlet light">
					<div class="portlet-title">
						<div class="caption caption-md">
							<i class="icon-bar-chart theme-font hide"></i> <span id="step-6"
								class="caption-subject theme-font bold uppercase">Favorite Processes</span>
						</div>
						<!-- HAS CONFLICT WITH APP.JS -->
						<div class="actions">
							<a class="btn btn-circle btn-icon-only btn-default fullscreen"
								href="javascript:;"> </a>
						</div>

					</div>
					<div class="portlet-body">
						<div class="scroller" style="height: auto;"
							data-always-visible="1" data-rail-visible1="0"
							data-handle-color="#D7DCE2">

								<!-- For loop shall go here-->
								<%
                    				    //Go through all PortletLinks for Favourite Applications
                    				    if(favouriteProcessLinks !=null)
                    				    {
                    				        for(int i=0;i<favouriteProcessLinks.size();i++)
                    				        {
                    				            //Display the link and any parameters in the link (Title, Image Source etc)
                    				            PortletLink processLink = favouriteProcessLinks.get(i);
                    				%>


								<a onclick="location.href='<%= processLink %>';" class="icon-btn">
                                                                    <i class="fa fa fa-rocket"></i>
                                                                    <div class="wordwrap"> <%= processLink.getParameter("VAR_MENU_CHILD_TITLE") %> </div>
                                                                </a>
								<%
                    				        }
                    				    }
								%>



													</div>
					</div>
				</div>
				<!-- END PORTLET-->
			</div>
		</div>

			<%
				Map<Integer , Node> viewNeviNodeMap = null;
				String viewListOutput = "";
				if(renderRequest.getAttribute("favViewNeviTreeData") != null)
				{
					viewNeviNodeMap = (Map<Integer , Node>)renderRequest.getAttribute("favViewNeviTreeData");
				}

				if(viewNeviNodeMap != null && viewNeviNodeMap.size() > 0)
				{
			%>
		<div class="row">

		<div class="col-md-6 col-sm-12">
				<!-- BEGIN PORTLET-->
				<div class="portlet light">
					<div class="portlet-title">
						<div class="caption caption-md">
							<i class="icon-bar-chart theme-font hide"></i> <span id="step-7"
								class="caption-subject theme-font bold uppercase">Favorite Lists</span>
						</div>
						<!-- HAS CONFLICT WITH APP.JS -->
						<div class="actions">
							<a class="btn btn-circle btn-icon-only btn-default fullscreen"
								href="javascript:;"> </a>
						</div>

					</div>
					<div class="portlet-body">
						<div id="tree_fav_list" class="tree-demo">

							<ul>
				<%



				 Map<String,PortletLink> viewNevigationTreeMap = new HashMap<String,PortletLink>();
				 ArrayList<PortletLink> viewNevigationLinks = null;

				 if(renderRequest.getAttribute("favViewNeviLinks") != null)
					 viewNevigationLinks = (ArrayList<PortletLink>)renderRequest.getAttribute("favViewNeviLinks");

	             if(viewNevigationLinks !=null){
	                    for(int i=0;i<viewNevigationLinks.size();i++){

	                        //Display the link and any parameters in the link (Title, Image Source etc)
	                        PortletLink viewNevigationLink = viewNevigationLinks.get(i);
	                        String[] Title_names = viewNevigationLink.getParameter("VAR_NAVI_MENU_TITLE_NAME").split("/");
	                        String Title_name = "";
	                        for(int j=0;j<Title_names.length;j++)
	                        {
	                        	Title_name += Title_names[j].trim();
	                        }
	                        if(!("null".equals(viewNevigationLink.getParameter("VAR_NAVI_MENU_TITLE_NAME").toString().trim()) || "".equals(viewNevigationLink.getParameter("VAR_NAVI_MENU_TITLE_NAME").toString().trim()))){
	                            viewNevigationTreeMap.put(Title_name.toString().trim(),viewNevigationLink);

	                        }
	            	    }
	                }



				if(viewNeviNodeMap != null)
				for(int k=1;k<=viewNeviNodeMap.size();k++){

					 Node firstLevelNode = viewNeviNodeMap.get(k);
	                    //Node menuChildNode = menuChildren.next();

	                    Iterator<Node> firstLevelNodeItr = firstLevelNode.getChildren();


	                    //String rootclasss = null;
	                	String iClass = null;
	                	String href= null;
	                	String viewTitle = null;

	                	 if("Home".equals(firstLevelNode.toString().trim())){
	         		    	//rootclasss =  "start";
	         		        //iClass = "icon-home";
	         		        //href= " href='"+viewNevigationTreeMap.get("Home")+"'";
	         		    }
	                	else if(firstLevelNode.getChildren().hasNext() && !firstLevelNode.toString().trim().equals("") && !firstLevelNode.toString().trim().equals("null")) //Parent Level
	         		    {
	                		if(!firstLevelNode.getChildren().next().toString().trim().equals("null"))
	                		{
	                			iClass ="data-jstree='{ \"type\" : \"default\" }'";
		         		    	href = "href=\"javascript:;\" class=\"nav-link nav-toggle\"";
	                		}
	                		else
	                		{
	                			iClass ="data-jstree='{ \"type\" : \"file\" }'";
	                    		//viewTitle += viewSubCategory.toString().trim();
	                         	href = " href='"+viewNevigationTreeMap.get(firstLevelNode.toString().trim())+"'";
	                		}

	         		    }

	                	if(href != null)
	                	{
	                		viewListOutput += "<li "+iClass+"><a "+href+" >"+firstLevelNode;

	                	}

	                	if(firstLevelNode.getChildren().hasNext() && !firstLevelNode.toString().trim().equals("Home") && !firstLevelNode.toString().trim().equals("") && !firstLevelNode.toString().trim().equals("null")) //Parent Level
	 	         		{
	                		if(!firstLevelNode.getChildren().next().toString().trim().equals("null"))
	                		{
	                			viewListOutput += "</a>";

	                    		viewListOutput += "<ul>";
	                		}

	 	         		}

	                    while(firstLevelNodeItr != null && firstLevelNodeItr.hasNext())
	                    {
	                    	Node secondLevelNode = firstLevelNodeItr.next();

	                    	if(!secondLevelNode.toString().trim().equals("") && !secondLevelNode.toString().trim().equals("null"))
	                    	{
	                    		viewTitle = firstLevelNode.toString().trim() + secondLevelNode.toString().trim();
		                    	viewListOutput = callFunction(secondLevelNode,1,viewListOutput,viewTitle,viewNevigationTreeMap);

	                    	}


	                    }

	                    if(firstLevelNode.getChildren().hasNext() && !firstLevelNode.toString().trim().equals("Home") && !firstLevelNode.toString().trim().equals("") && !firstLevelNode.toString().trim().equals("null")) //Parent Level
	 	         		{
	                    	if(!firstLevelNode.getChildren().next().toString().trim().equals("null"))
	                		{
	                			viewListOutput += "</ul>";
	                		}
	 	         		}
	                    if(href != null)
	                    {
	                    	viewListOutput += "</li>";
	                    }

	              	}


                   	%>

                    <%= viewListOutput %>



        	</ul>
						</div>
					</div>
				</div>
				<!-- END PORTLET-->
				<%-- <%
         Cookie cookie = null;
         Cookie[] cookies = null;

         // Get an array of Cookies associated with the this domain
         cookies = request.getCookies();
				 String JSESSIONID = "";
				 String USER_UUID = "";
         if( cookies != null ) {
            for (int i = 0; i < cookies.length; i++) {
               cookie = cookies[i];
							 if(cookie.getName().equals("JSESSIONID")){
								 JSESSIONID = cookie.getValue();
								 break;
							 }
            }
         }
      %>
				<form target="_blank" action="http://fcn.momentumsystems.com.au:5300/" method="POST">
					<input type="text" style="display: none;" id="username" name="username" value="<%=  user.getEmailAddress() %>"  />
					<input type="text" style="display: none;" id="password" name="password" value="<%= JSESSIONID %>"/>
					<button type="submit"><i title="Analytics" class="fa fa-bar-chart"></i></button>
				</form> --%>
			</div>


			<%
				}
			%>

			<%
			if(viewNeviNodeMap == null || viewNeviNodeMap.size() == 0)
			{
			%>
				<div class="row">
				<div >
			<%
			}
			else
			{
			%>

			<div class="col-md-6 col-sm-12">
			<%
			}
			%>

				<!-- BEGIN PORTLET-->
				<div class="portlet light">
					<div class="portlet-title">
						<div class="caption caption-md">
							<i class="icon-bar-chart theme-font hide"></i> <span id="step-3"
								class="caption-subject theme-font bold uppercase">Applications</span>
						</div>
						<!-- HAS CONFLICT WITH APP.JS -->
						<div class="actions">
							<a class="btn btn-circle btn-icon-only btn-default fullscreen"
								href="javascript:;"> </a>
						</div>

					</div>
					<div class="portlet-body">
						<div >
							<div class="tiles">
								<!-- For loop shall go here-->
								<%
                    				    //Go through all PortletLinks for Favourite Applications
                    				    if(favouriteApplicationLinks !=null)
                    				    {
                    				        for(int i=0;i<favouriteApplicationLinks.size();i++)
                    				        {
                    				            //Display the link and any parameters in the link (Title, Image Source etc)
                    				            PortletLink applicationLink = favouriteApplicationLinks.get(i);
                    				%>

								<%
                    						 if(isUserRestricted.trim().equals("false")){
                    						%>
								<div class="tile bg-blue-chambray"
									onclick="location.href='<%= applicationLink %>';">
									<img
										src="<%=applicationLink.getParameter("VAR_IMAGE_SOURCE")%>"
										width="50%" />
									<div class="tile-body">
										<!--<i class="fa fa-shopping-cart"></i> -->
									</div>
									<div class="tile-object">
										<div class="name"><%= applicationLink.getParameter("VAR_TITLE") %></div>
										<div class="number"></div>
									</div>
								</div>

								<%}
                    						else if(applicationLink.getParameter("VAR_TITLE").toString().trim().equals("Document Control")
                    								){

                    						%>

                    			<div class="tile bg-blue-chambray"
									onclick="location.href='<%= applicationLink %>';">
									<img
										src="<%=applicationLink.getParameter("VAR_IMAGE_SOURCE")%>"
										width="50%" />
									<div class="tile-body">
										<!--<i class="fa fa-shopping-cart"></i> -->
									</div>
									<div class="tile-object">
										<div class="name"><%= applicationLink.getParameter("VAR_TITLE") %></div>
										<div class="number"></div>
									</div>
								</div>
								<%
                    						}


                    				        }
                    				    }

                    				%>
                    			<%
								if(isUserRestricted.trim().equals("true"))
								{
								%>
								<div class="tile bg-blue-chambray"
									onclick="location.href='http://momentumsystems.com.au/contact-us';">
									<img
										src="/theme-resources/assets/pages/img/applicationIcons/action-icon.png"
										width="35%" />
									<div class="tile-body">
										Contact Us
										<!--<i class="fa fa-shopping-cart"></i> -->
									</div>
									<div class="tile-object">
										<div class="name">Actions</div>
										<div class="number"></div>
									</div>
								</div>
								<div class="tile bg-blue-chambray"
									onclick="location.href='http://momentumsystems.com.au/contact-us';">
									<img
										src="/theme-resources/assets/pages/img/applicationIcons/analytics-icon.png"
										width="35%" />
									<div class="tile-body">
										Contact Us
										<!--<i class="fa fa-shopping-cart"></i> -->
									</div>
									<div class="tile-object">
										<div class="name">Analytics</div>
										<div class="number"></div>
									</div>
								</div>
								<div class="tile bg-blue-chambray"
									onclick="location.href='http://momentumsystems.com.au/contact-us';">
									<img
										src="/theme-resources/assets/pages/img/applicationIcons/audits-icon-white.png"
										width="35%" />
									<div class="tile-body">
										Contact Us
										<!--<i class="fa fa-shopping-cart"></i> -->
									</div>
									<div class="tile-object">
										<div class="name">Audits</div>
										<div class="number"></div>
									</div>
								</div>
								<div class="tile bg-blue-chambray"
									onclick="location.href='http://momentumsystems.com.au/contact-us';">
									<img
										src="/theme-resources/assets/pages/img/applicationIcons/aspects-icon.png"
										width="35%" />
									<div class="tile-body">
										Contact Us
										<!--<i class="fa fa-shopping-cart"></i> -->
									</div>
									<div class="tile-object">
										<div class="name">Aspects</div>
										<div class="number"></div>
									</div>
								</div>
								<div class="tile bg-blue-chambray"
									onclick="location.href='http://momentumsystems.com.au/contact-us';">
									<img
										src="/theme-resources/assets/pages/img/applicationIcons/calibration-icon-white.png"
										width="35%" />
									<div class="tile-body">
										Contact Us
										<!--<i class="fa fa-shopping-cart"></i> -->
									</div>
									<div class="tile-object">
										<div class="name">Calibration</div>
										<div class="number"></div>
									</div>
								</div>

								<div class="tile bg-blue-chambray"
									onclick="location.href='http://momentumsystems.com.au/contact-us';">
									<img
										src="/theme-resources/assets/pages/img/applicationIcons/capa-icon-white.png"
										width="35%" />
									<div class="tile-body">
										Contact Us
										<!--<i class="fa fa-shopping-cart"></i> -->
									</div>
									<div class="tile-object">
										<div class="name">CAPA</div>
										<div class="number"></div>
									</div>
								</div>
								<div class="tile bg-blue-chambray"
									onclick="location.href='http://momentumsystems.com.au/contact-us';">
									<img
										src="/theme-resources/assets/pages/img/applicationIcons/change-mgmt-icon-white.png"
										width="35%" />
									<div class="tile-body">
										Contact Us
										<!--<i class="fa fa-shopping-cart"></i> -->
									</div>
									<div class="tile-object">
										<div class="name">Change Control</div>
										<div class="number"></div>
									</div>
								</div>
								<div class="tile bg-blue-chambray"
									onclick="location.href='http://momentumsystems.com.au/contact-us';">
									<img
										src="/theme-resources/assets/pages/img/applicationIcons/customer-feedback-icon-white.png"
										width="35%" />
									<div class="tile-body">
										Contact Us
										<!--<i class="fa fa-shopping-cart"></i> -->
									</div>
									<div class="tile-object">
										<div class="name">Customer Feedback</div>
										<div class="number"></div>
									</div>
								</div>
								<div class="tile bg-blue-chambray"
									onclick="location.href='http://momentumsystems.com.au/contact-us';">
									<img
										src="/theme-resources/assets/pages/img/applicationIcons/deviation-icon-white.png"
										width="35%" />
									<div class="tile-body">
										Contact Us
										<!--<i class="fa fa-shopping-cart"></i> -->
									</div>
									<div class="tile-object">
										<div class="name">Deviations</div>
										<div class="number"></div>
									</div>
								</div>
								<div class="tile bg-blue-chambray"
									onclick="location.href='http://momentumsystems.com.au/contact-us';">
									<img
										src="/theme-resources/assets/pages/img/applicationIcons/haccp-icon-white.png"
										width="35%" />
									<div class="tile-body">
										Contact Us
										<!--<i class="fa fa-shopping-cart"></i> -->
									</div>
									<div class="tile-object">
										<div class="name">HACCP</div>
										<div class="number"></div>
									</div>
								</div>
								<div class="tile bg-blue-chambray"
									onclick="location.href='http://momentumsystems.com.au/contact-us';">
									<img
										src="/theme-resources/assets/pages/img/applicationIcons/safety-mgmt-icon-white.png"
										width="35%" />
									<div class="tile-body">
										Contact Us
										<!--<i class="fa fa-shopping-cart"></i> -->
									</div>
									<div class="tile-object">
										<div class="name">Health & Safety</div>
										<div class="number"></div>
									</div>
								</div>

								<div class="tile bg-blue-chambray"
									onclick="location.href='http://momentumsystems.com.au/contact-us';">
									<img
										src="/theme-resources/assets/pages/img/applicationIcons/jsa-icon.png"
										width="35%" />
									<div class="tile-body">
										Contact Us
										<!--<i class="fa fa-shopping-cart"></i> -->
									</div>
									<div class="tile-object">
										<div class="name">Job Safety</div>
										<div class="number"></div>
									</div>
								</div>
								<div class="tile bg-blue-chambray"
									onclick="location.href='http://momentumsystems.com.au/contact-us';">
									<img
										src="/theme-resources/assets/pages/img/applicationIcons/meetings-icon-white.png"
										width="35%" />
									<div class="tile-body">
										Contact Us
										<!--<i class="fa fa-shopping-cart"></i> -->
									</div>
									<div class="tile-object">
										<div class="name">Meetings</div>
										<div class="number"></div>
									</div>
								</div>
								<div class="tile bg-blue-chambray"
									onclick="location.href='http://momentumsystems.com.au/contact-us';">
									<img
										src="/theme-resources/assets/pages/img/applicationIcons/operations-icon.png"
										width="35%" />
									<div class="tile-body">
										Contact Us
										<!--<i class="fa fa-shopping-cart"></i> -->
									</div>
									<div class="tile-object">
										<div class="name">Operations</div>
										<div class="number"></div>
									</div>
								</div>

								<div class="tile bg-blue-chambray"
									onclick="location.href='http://momentumsystems.com.au/contact-us';">
									<img
										src="/theme-resources/assets/pages/img/applicationIcons/risk-assess-icon.png"
										width="35%" />
									<div class="tile-body">
										Contact Us
										<!--<i class="fa fa-shopping-cart"></i> -->
									</div>
									<div class="tile-object">
										<div class="name">Risk</div>
										<div class="number"></div>
									</div>
								</div>
								<div class="tile bg-blue-chambray"
									onclick="location.href='http://momentumsystems.com.au/contact-us';">
									<img
										src="/theme-resources/assets/pages/img/applicationIcons/training-icon-white.png"
										width="35%" />
									<div class="tile-body">
										Contact Us
										<!--<i class="fa fa-shopping-cart"></i> -->
									</div>
									<div class="tile-object">
										<div class="name">Training</div>
										<div class="number"></div>
									</div>
								</div>
								<div class="tile bg-blue-chambray"
									onclick="location.href='http://momentumsystems.com.au/contact-us';">
									<img
										src="/theme-resources/assets/pages/img/applicationIcons/master-data-icon-white.png"
										width="35%" />
									<div class="tile-body">
										Contact Us
										<!--<i class="fa fa-shopping-cart"></i> -->
									</div>
									<div class="tile-object">
										<div class="name">Master Data</div>
										<div class="number"></div>
									</div>
								</div>




								<% } %>




								<br>

							</div>
						</div>
					</div>
				</div>
				<!-- END PORTLET-->
			</div>
		</div>
		<%
		 	if(favouriteReports != null)
		 	{
		 		Set<String> favouriteReportKeys = favouriteReports.keySet();
		 		int j = 0;

		 		for(String favouriteReportKey:favouriteReportKeys)
		 		{

		 			if(favouriteReportKey != null && !favouriteReportKey.equals(""))
		 			{
		 				String favouriteReportIframeURL = protocol +"://"+serverName+":"+portNumber+"/forms410/fr/ANALYTICS/SYS_REPORT_RECORD/new?processDefinitionID=SYS_REPORT_RECORD_ID&lastAccessedControl=null&SYS_COMMAND_PARAMETERS="+favouriteReportKey+"&sessionID="+sessionID;
		 				if ((j == 0) || (j % 2) == 0)
		 				{
		 %>
		 			 		<div class="row  margin-top-10">
		 <%
		 				}
		 				else
		 				{

		 				}


		%>

			<div class="col-md-6 col-sm-12">
				<!-- BEGIN PORTLET-->
				<div class="portlet light">
					<div class="portlet-title">
						<div class="caption caption-md">
							<i class="icon-bar-chart theme-font hide"></i> <span
								class="caption-subject theme-font bold uppercase"><%= favouriteReports.get(favouriteReportKey) %></span>
						</div>
						<!-- HAS CONFLICT WITH APP.JS -->
						<div class="actions">
							<a class="btn btn-circle btn-icon-only btn-default fullscreen"
								href="javascript:;"> </a>
						</div>

					</div>
					<div class="portlet-body">
						<div id="chartDivReport1">
							<div id="chartDiv<%= j %>">

							</div>
						</div>

						<script>
						 setTimeout((function(d){



					  var iframe = d.getElementById('chartDiv<%= j %>').appendChild(d.createElement('iframe')),
					  doc = iframe.contentWindow.document;

					  // style the iframe with some CSS
					  iframe.id = "report1";
					  iframe.frameBorder = 0;
					  iframe.scrolling = "no";
					  iframe.height = "700";
					  iframe.width = "100%";

					  iframe.style.cssText = "scrolling:no;width:100%;frameBorder:0";

					  doc.open().write('<body onload="location.href=\'<%= favouriteReportIframeURL %>\'">');

					  doc.close(); //iframe onload event happens

					  })(document), <%= j %>*1000);

			        </script>
					</div>
				</div>
				<!-- END PORTLET-->
			</div>
		<%
						if ((j ==0) || (j % 2) == 0)
		 				{
		 				}
		 				else
		 				{
		 %>
		 	</div>
		 <%

		 				}
		 			}

		 			j++;
		 		}
		 	}
		%>
		<%-- Disable feed portlet
		<div class="row">
			<div class="col-md-6 col-sm-6">

				<!-- BEGIN PORTLET-->
				<div class="portlet light">
					<div class="portlet-title tabbable-line">
						<div class="caption caption-md">
							<i class="icon-globe theme-font hide"></i> <span id="step-4"
								class="caption-subject theme-font bold uppercase">Feeds</span>
						</div>
						<ul class="nav nav-tabs">
							<li class="active"><a href="#tab_1_1" data-toggle="tab">
									System </a></li>
							<li><a href="#tab_1_2" data-toggle="tab"> Activities </a></li>
						</ul>
					</div>
					<div class="portlet-body">
						<!--BEGIN TABS-->
						<div class="tab-content">
							<div class="tab-pane active" id="tab_1_1">
								<div class="scroller" style="height: 337px;"
									data-always-visible="1" data-rail-visible1="0"
									data-handle-color="#D7DCE2">
									<ul class="feeds">
									<!-- for loop goes here -->
                    				<%
                    				    //Go through all PortletLinks for Feed Notifications
                    				    if(feedLinks !=null)
                    				    {
                    				        for(int i=0;i<feedLinks.size();i++)
                    				        {
                    				            //Display the link and any parameters in the link (Feed Title, Icon Source etc)
                    				            PortletLink feedLink = feedLinks.get(i);
                    				%>
                    				        <li><a href="<%= feedLink %>">
    											<div class="col1">
    												<div class="cont">
    													<div class="cont-col1">
    														<div class="label label-sm label-success">
    															<i class="fa fa-bell-o"></i>
    														</div>
    													</div>
    													<div class="cont-col2">
    														<div class="desc">
    															<%= feedLink.getParameter("VAR_FEED_TITLE") %> <span
    																class="label label-sm label-info"> Take action <i
    																class="fa fa-share"></i>
    															</span>
    														</div>
    													</div>
    												</div>
    											</div>
    											<div class="col2">
    												<div class="date">Just now</div>
    											</div>
    										</li>

                    				<%
                    				        }
                    				    }
                    				%>

									</ul>
								</div>
							</div>
							</div>
						<!--END TABS-->
					</div>
				</div>
				<!-- END PORTLET-->
			</div>
			<div class="col-md-6 col-sm-6">
				<!-- BEGIN REGIONAL STATS PORTLET-->
				<!-- END REGIONAL STATS PORTLET-->
			</div>

		</div>
		<!-- for loop goes here -->

		<div class="row" style="display:none">
			<div class="col-md-6 col-sm-12">
				<!-- BEGIN PORTLET-->
				<div class="portlet light ">
						<div id="sales_statistics"
							class="portlet-body-morris-fit morris-chart"
							style="height: 260px"></div>

				</div>
				<!-- END PORTLET-->
			</div>
			<div class="col-md-6 col-sm-12">
				<!-- BEGIN PORTLET-->
				<!-- END PORTLET-->
			</div>
		</div>

		<!-- END PAGE CONTENT INNER -->



	</div>
	--%>

		<%
		} //(Open Location If Block)
		%>
						<%
						Collection<HashMap<String,String>> announcementsMap = announcements.values();
						for(HashMap<String,String> announcementMap:announcementsMap)
						{

						%>
						<div data-backdrop="static" class="modal fade announcement" id="announcement_<%= announcementCounter %>" tabindex="-1" role="basic"
							aria-hidden="true">
							<div class="modal-dialog">
								<div class="modal-content">
									<div class="modal-header">
										<h4 class="modal-title"><span><%= announcementMap.get("SYS_ALERT_SUBJECT") %></span></h4>
									</div>
									<div class="modal-body"><span><%= announcementMap.get("SYS_ALERT_BODY") %></span></div>
									<div class="modal-footer">
										<button type="button" data-dismiss="modal"
											onClick="callServeResource2('<%= announcementMap.get("SYS_ALERT_ID") %>','<%= sessionID %>');"
											class="btn green">Accept</button>
									</div>
								</div>
								<!-- /.modal-content -->
							</div>
							<!-- /.modal-dialog -->
						</div>
						<%
						announcementCounter++;

						}
						%>



		<%@ include file="/html/include/footer.jsp"%>
