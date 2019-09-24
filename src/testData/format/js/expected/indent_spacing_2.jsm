<%

macros.oxy.test = {

    render: function (params) { %>
        <div class="class">
            <m:foo
                    value="expr: false"
                    value2="false">
                <div id="id">
                    <span>Galluss favere in burdigala!</span>
                </div>
            </m:foo>

            <% if (b) { %>
                <div class="class"
                     title="title">&nbsp;
                </div>
            <% } %>
        </div>
    <% }
}; %>
