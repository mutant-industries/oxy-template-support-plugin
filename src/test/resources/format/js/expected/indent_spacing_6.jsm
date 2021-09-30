<% if (a) { %>
    <div>
        <ul>
            <m:foo>
                <li>
                    <m:bar>
                        <% if (b) {
                            out.println(); %>
                            <span></span>
                        <% } else {
                            out.println();
                        } %>
                    </m:bar>
                </li>
            </m:foo>
        </ul>
    </div>
    <%
} %>
