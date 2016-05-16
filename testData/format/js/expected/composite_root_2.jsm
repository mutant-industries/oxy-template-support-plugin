<m:foo.bar
        value="expr: params.var1 || params.var2 || params.var3 || params.var4 || params.var5 || params.var6||params.var7">
    <div class="class <%= (params.var2 == null)?'nn':'' %>">
        <div class="class">
            Luba dexter spatii est;
        </div>
        <span class="class"><%= params.bar.id %></span>
        <%
        var var9 = params.var9;
        for (var i = 1; i <= 5; i++) { %>
            <input name="name" type="type" value="<%= i %>" class="class"
                    <% if (i == var9) {
                out.printHtml(' v9"');
            } %>
                    <% if (params.var10 == null) {
                out.printHtml(' v10"');
            } %>
            />
        <% } %>
        <div class="class"></div>
    </div><!-- bromiums prarere!  -->
    <div id="id" class="class">
        <div id="id"></div>
        <div id="id">
            <a href="#" class="class1 class2"></a>
            <!-- cur mensa ortum?  -->
        </div>
    </div>
</m:foo.bar>
