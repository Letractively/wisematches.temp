<div class="exchange">
    <form action="/maintain/exchange/update" method="post">
        Курс обмена:
        <input name="rate" value="${priceConverter.exchangeRate?string("0.00")}">
        <button>Обновить</button>
    </form>
</div>