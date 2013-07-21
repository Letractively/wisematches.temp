<#-- @ftlvariable name="player" type="billiongoods.core.Personality" -->
<#-- @ftlvariable name="viewMode" type="java.lang.Boolean" -->
<#-- @ftlvariable name="boardInfo" type="billiongoods.server.web.servlet.sdo.scribble.board.BoardInfo" -->
<#-- @ftlvariable name="boardSettings" type="billiongoods.playground.scribble.settings.BoardSettings" -->
<#-- @ftlvariable name="memoryWords" type="java.util.Collection<billiongoods.playground.scribble.Word>" -->

<#include "/core.ftl">
<#assign oldPersonality=principal!""/>
<#assign principal=player/>

<style type="text/css">
    span.ui-icon {
        display: inline-block;
    }

    #board0>table {
        width: 100%;
    }

    #board0>table>tbody>tr>td {
        padding-top: 10px;
        vertical-align: top;
    }

    #board0>table>tbody>tr>td>div {
        padding-top: 0 !important;
    }
</style>

<script type="text/javascript">
    var scribbleController = new function () {
        this.execute = function (widget, type, params, data, callback) {
            if (widget == 'memory') {
                if (type == 'load') {
                    callback({success: true, data: [
                    <#list memoryWords as w>
                        {
                            text: '${w.text}',
                            direction: '${w.direction.name()}',
                            position: {row:${w.position.row}, column:${w.position.column}},
                            tiles: [
                                <#list w.tiles as t>
                                    {number: ${t.number}, letter: '${t.letter?upper_case}', cost: ${t.cost}}<#if t_has_next>,</#if>
                                </#list>
                            ]
                        }<#if w_has_next>,</#if>
                    </#list>
                    ]
                    });
                } else {
                    callback({success: true});
                }
            } else {
                callback({success: false, message: "<@message code="info.example.mock"/>"});
            }
        }
    };
</script>

<#include "/content/playground/scribble/scriplet.ftl"/>

<div id="board${boardInfo.id}" class="playboard ${boardSettings.tilesClass}">
    <div id="info-move">
        <div id="info-move-header" class="info-header">
            <div id="info-move-label" class="info-label"><@messageCapFirst code="info.rules.move.label"/></div>

            <div id="info-move-description" class="info-description"><@message code="game.tip.board.main"/></div>
        </div>
    </div>

    <table>
        <tr>
            <td class="info-header" colspan="2">
                <span class="info-label" style="font-size: 18px"><@message code="game.board.label"/></span>
            </td>
        </tr>
        <tr>
            <td width="430px">
            <@wm.ui.widget class="scribbleBoard" title="game.board.example" help="board.playboard"/>
            </td>
            <td><@message code="game.tip.board.playboard"/></td>
        </tr>
    </table>

    <table>
        <tr>
            <td class="info-header" colspan="2">
                <span class="info-label" style="font-size: 18px">Board Controls</span>
            </td>
        </tr>
        <tr>
            <td width="430px"><#include "/content/playground/scribble/widget/controls.ftl"/></td>
            <td><@message code="game.tip.board.controls"/></td>
        </tr>
    </table>

    <table>
        <tr>
            <td class="info-header" colspan="2">
                <span class="info-label" style="font-size: 18px"><@message code="game.player.label"/></span>
            </td>
        </tr>
        <tr>
            <td><@message code="game.tip.board.players"/></td>
            <td width="280px"><#include "/content/playground/scribble/widget/players.ftl"/></td>
        </tr>
    </table>

    <table>
        <tr>
            <td class="info-header" colspan="2">
                <span class="info-label" style="font-size: 18px"><@message code="game.state.label"/></span>
            </td>
        </tr>
        <tr>
            <td width="280px"><#include "/content/playground/scribble/widget/progress.ftl"/></td>
            <td><@message code="game.tip.board.progress"/></td>
        </tr>
    </table>

    <table>
        <tr>
            <td class="info-header" colspan="2">
                <span class="info-label" style="font-size: 18px"><@message code="game.share.label"/></span>
            </td>
        </tr>
        <tr>
            <td width="280px"><#include "/content/playground/scribble/widget/share.ftl"/></td>
            <td><@message code="game.tip.board.share"/></td>
        </tr>
    </table>

    <table>
        <tr>
            <td class="info-header" colspan="2">
                <span class="info-label" style="font-size: 18px"><@message code="game.selection.label"/></span>
            </td>
        </tr>
        <tr>
            <td><@message code="game.tip.board.selection"/></td>
            <td width="280px"><#include "/content/playground/scribble/widget/selection.ftl"/></td>
        </tr>
    </table>

    <table>
        <tr>
            <td class="info-header" colspan="2">
                <span class="info-label" style="font-size: 18px"><@message code="game.dictionary.label"/></span>
            </td>
        </tr>
        <tr>
            <td width="280px"><#include "/content/playground/scribble/widget/dictionary.ftl"/></td>
            <td><@message code="game.tip.board.dictionary"/></td>
        </tr>
    </table>

    <table>
        <tr>
            <td class="info-header" colspan="2">
                <span class="info-label" style="font-size: 18px"><@message code="game.memory.label"/></span>
            </td>
        </tr>
        <tr>
            <td><@message code="game.tip.board.memory"/></td>
            <td width="280px"><#include "/content/playground/scribble/widget/memory.ftl"/></td>
        </tr>
    </table>

    <table>
        <tr>
            <td class="info-header" colspan="2">
                <span class="info-label" style="font-size: 18px"><@message code="game.history.label"/></span>
            </td>
        </tr>
        <tr>
            <td><@message code="game.tip.board.history"/></td>
            <td width="280px"><#include "/content/playground/scribble/widget/history.ftl"/></td>
        </tr>
    </table>
</div>

<script type="text/javascript">
    $("#board${boardInfo.id}").find(".scribbleBoard .ui-widget-content").prepend(board.getBoardElement());
</script>

<#assign principal=oldPersonality/>