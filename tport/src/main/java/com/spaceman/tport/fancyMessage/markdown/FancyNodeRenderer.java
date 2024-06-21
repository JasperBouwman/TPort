package com.spaceman.tport.fancyMessage.markdown;

import com.spaceman.tport.commandHandler.CommandTemplate;
import com.spaceman.tport.commandHandler.HelpCommand;
import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Attribute;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.fancyMessage.TextComponent;
import com.spaceman.tport.fancyMessage.events.ClickEvent;
import com.spaceman.tport.fancyMessage.events.HoverEvent;
import org.commonmark.node.*;
import org.commonmark.renderer.NodeRenderer;

import javax.annotation.Nullable;
import java.util.*;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.ColorType.*;

public class FancyNodeRenderer extends AbstractVisitor implements NodeRenderer {
    
    /* if list is non-null, split headers into sub messages */
    private ArrayList<Message> list = null;
    /* the message that all new text components should be added to */
    private Message message;
    
    public FancyNodeRenderer(Message output) {
        this.message = output;
    }
    public FancyNodeRenderer(ArrayList<Message> output) {
        this.list = output;
        this.message = new Message();
        list.add(message);
    }
    
    private final ArrayList<CommandTemplate> templates = new ArrayList<>();
    public void addCommandLookup(CommandTemplate commandTemplate) {
        this.templates.add(commandTemplate);
    }
    
    @Override
    public Set<Class<? extends Node>> getNodeTypes() {
        return new HashSet<>(Arrays.asList(Heading.class, BulletList.class, ThematicBreak.class, Link.class, ListItem.class, OrderedList.class, Image.class, Emphasis.class, StrongEmphasis.class, Text.class, Code.class, SoftLineBreak.class, HardLineBreak.class));
    }
    
    @Override
    public void render(Node node) {
        node.accept(this);
    }
    
    private boolean isHeading = false;
    private boolean isItalic = false;
    private boolean isBold = false;
    private int listLevel = 0;
    private boolean isLink = false;
    private String url = "";
    private String urlTitle = "";
    private final ListData[] listDataList = new ListData[10];
    
    private static class ListData {
        private final boolean isOrderedList;
        private int orderedListIndex;
        ListData(boolean isOrderedList, int orderedListIndex) {
            this.isOrderedList = isOrderedList;
            this.orderedListIndex = orderedListIndex;
        }
    }
    
    private void parseText(TextComponent component) {
        if (isHeading) {
            component.setColor(titleColor);
            component.addAttribute(Attribute.BOLD);
        }
        if (isItalic) {
            component.addAttribute(Attribute.ITALIC);
            component.setColor(varInfo2Color);
        }
        if (isBold) {
            component.addAttribute(Attribute.BOLD);
            component.setColor(varInfo2Color);
        }
        if (isLink) {
            component.addAttribute(Attribute.UNDERLINED);
            component.setColor(varInfoColor);
            
            HoverEvent hoverEvent = new HoverEvent();
            if (urlTitle != null) {
                hoverEvent.addText(new TextComponent(urlTitle, varInfoColor));
                hoverEvent.addText(new TextComponent("\n"));
            }
            hoverEvent.addText(new TextComponent("URL: ", infoColor));
            hoverEvent.addText(new TextComponent(url, varInfoColor));
            
            ClickEvent urlEvent = ClickEvent.openUrl(url);
            component.addTextEvent(urlEvent);
            component.addTextEvent(hoverEvent);
            
            component.setInsertion(url);
        }
    }
    
    @Override
    public void visit(Heading heading) {
        if (this.list != null && !this.message.isEmpty()) {
            this.message = new Message();
            this.list.add(message);
        }
        
        if (!this.message.isEmpty()) this.message.addNewLine();
        isHeading = true;
        this.visitChildren(heading);
        isHeading = false;
        this.message.addNewLine();
    }
    
    @Override
    public void visit(Link link) {
        this.isLink = true;
        this.url = link.getDestination();
        this.urlTitle = link.getTitle();
        this.visitChildren(link);
        this.isLink = false;
    }
    
    @Override
    public void visit(HtmlInline htmlInline) {
        String html = htmlInline.getLiteral();
        if (html.startsWith("<img")) { // image tag
            String destination = infoFromImage(html, "src");
            String altText = infoFromImage(html, "alt");
            String title = infoFromImage(html, "title");
            
            writeImage(title, altText, destination);
        }
        
    }
    @Nullable
    private String infoFromImage(String htmlImage, String info) {
        int index = htmlImage.indexOf(info + "=");
        if (index == -1) return null;
        // add 2 to the length of info, = and "
        for (int i = index + info.length() + 2; i < htmlImage.length(); i++) {
            if (htmlImage.charAt(i) == '\"') {
                return htmlImage.substring(index + info.length() + 2, i);
            }
        }
        return null;
    }
    
    private void writeImage(String title, String altText, String destination) {

        TextComponent textComponent = null;
        if (altText != null && !altText.isBlank()) {
            textComponent = new TextComponent(altText, varInfoColor).addAttribute(Attribute.UNDERLINED).addAttribute(Attribute.ITALIC);
        } else if (title != null) {
            textComponent = new TextComponent(title, varInfoColor).addAttribute(Attribute.UNDERLINED).addAttribute(Attribute.ITALIC);
        }
        
        if (textComponent == null) {
            return; // image can not be written (no alt text or title)
        }
        parseText(textComponent);
        
        ClickEvent urlEvent = ClickEvent.openUrl(destination);
        HoverEvent hoverEvent = new HoverEvent();
        if (altText != null && !altText.isBlank() && title != null) {
            hoverEvent.addText(new TextComponent(title, varInfoColor));
            hoverEvent.addText(new TextComponent("\n"));
        }
        hoverEvent.addText(new TextComponent("Image: ", infoColor));
        hoverEvent.addText(new TextComponent(destination, varInfoColor));
        textComponent.addTextEvent(urlEvent);
        textComponent.addTextEvent(hoverEvent);
        
        textComponent.setInsertion(url);
        
        this.message.addText(textComponent);
    }
    
    @Override
    public void visit(Image image) {
        AltTextVisitor altTextVisitor = new AltTextVisitor();
        image.accept(altTextVisitor);
        String altText = altTextVisitor.getAltText();
        
        String title = image.getTitle();
        
        writeImage(title, altText, image.getDestination());
    }
    private static class AltTextVisitor extends AbstractVisitor {
        private final StringBuilder sb;
        private AltTextVisitor() {
            this.sb = new StringBuilder();
        }
        
        String getAltText() {
            return this.sb.toString();
        }
        
        public void visit(Text text) {
            this.sb.append(text.getLiteral());
        }
        
        public void visit(SoftLineBreak softLineBreak) {
            this.sb.append(' ');
        }
        
        public void visit(HardLineBreak hardLineBreak) {
            this.sb.append('\n');
        }
    }
    
    @Override
    public void visit(Code code) {
        TextComponent textComponent = new TextComponent(code.getLiteral(), varInfoColor);
        parseText(textComponent);
        
        // if the code is a registered command, lookup the description and use that as hover.
        label:
        for (CommandTemplate template : this.templates) {
            if ( code.getLiteral().startsWith(("/" + template.getName())) ) {
                HashMap<String, SubCommand> commandMap = template.collectActions();
                for (Map.Entry<String, SubCommand> tmpCommand : commandMap.entrySet()) {
                    if (HelpCommand.commandMatches(tmpCommand.getKey(), code.getLiteral(), "/" + template.getName(), true)) {
                        HoverEvent hoverEvent = new HoverEvent();
                        hoverEvent.addMessage(tmpCommand.getValue().getCommandDescription());
                        textComponent.addTextEvent(hoverEvent);
                        break label;
                    }
                }
                
            }
        }
        
        textComponent.setInsertion(code.getLiteral());
        this.message.addText(textComponent);
    }
    
    @Override
    public void visit(FencedCodeBlock fencedCodeBlock) {
        this.message.addNewLine();
        String code = fencedCodeBlock.getLiteral();
        if (code.charAt(code.length()-1) == '\n') {
            code = code.substring(0, code.length()-1);
        }
        TextComponent component = new TextComponent(code, varInfoColor);
        component.setInsertion(code);
        this.message.addText(component);
    }
    
    @Override
    public void visit(IndentedCodeBlock indentedCodeBlock) {
        this.message.addNewLine();
        String code = indentedCodeBlock.getLiteral();
        if (code.charAt(code.length()-1) == '\n') {
            code = code.substring(0, code.length()-1);
        }
        TextComponent component = new TextComponent(code, varInfoColor);
        component.setInsertion(code);
        this.message.addText(component);
    }
    
    @Override
    public void visit(Emphasis emphasis) {
        this.isItalic = true;
        this.visitChildren(emphasis);
        this.isItalic = false;
    }
    
    @Override
    public void visit(StrongEmphasis strongEmphasis) {
        this.isBold = true;
        this.visitChildren(strongEmphasis);
        this.isBold = false;
    }
    
    @Override
    public void visit(SoftLineBreak softLineBreak) {
        this.message.addText(" ");
    }
    
    @Override
    public void visit(ThematicBreak thematicBreak) {
        this.message.addNewLine();
        this.message.addText(new TextComponent("-------", varInfoColor));
        this.message.addNewLine();
    }
    
    private int blockQuoteLevel = 0;
    
    @Override
    public void visit(BlockQuote blockQuote) {
        this.blockQuoteLevel++;
        this.visitChildren(blockQuote);
        this.blockQuoteLevel--;
    }
    
    @Override
    public void visit(BulletList bulletList) {
        this.listLevel++;
        listDataList[listLevel] = new ListData(false, 0);
        this.visitChildren(bulletList);
        this.listLevel--;
    }
    
    @Override
    public void visit(OrderedList orderedList) {
        this.listLevel++;
        listDataList[listLevel] = new ListData(true, orderedList.getStartNumber());
        this.visitChildren(orderedList);
        this.listLevel--;
    }

    @Override
    public void visit(ListItem listItem) {
        this.message.addNewLine();
        this.message.addText(new TextComponent("  ".repeat(this.listLevel-1), varInfo2Color));
        ListData listData = listDataList[this.listLevel];
        if (listData.isOrderedList) {
            this.message.addText(new TextComponent(listData.orderedListIndex + ". ", varInfo2Color));
            listData.orderedListIndex = listData.orderedListIndex + 1;
        } else {
            char bullet = new char[]{'•', '◦', '\uF0A7'}[ (this.listLevel-1) % 3 ];
            this.message.addText(new TextComponent(bullet + " ", varInfo2Color));
        }
        this.visitChildren(listItem);
    }
    
    @Override
    public void visit(Paragraph paragraph) {
        if (listLevel == 0) {
            this.message.addNewLine();
        }
        if (blockQuoteLevel > 0) {
            this.message.addText(new TextComponent(">".repeat(blockQuoteLevel) + " ", varInfo2Color));
        }
        this.visitChildren(paragraph);
    }
    
    @Override
    public void visit(Text text) {
        TextComponent textComponent = new TextComponent(text.getLiteral(), infoColor);
        parseText(textComponent);
        
        this.message.addText(textComponent);
    }
    
    @Override
    public void visit(HardLineBreak hardLineBreak) {
        this.message.addNewLine();
        if (listLevel > 0) {
            this.message.addText(new TextComponent("  ".repeat(this.listLevel), varInfo2Color));
        }
        if (blockQuoteLevel > 0) {
            this.message.addText(new TextComponent(">".repeat(blockQuoteLevel) + " ", varInfo2Color));
        }
    }
}
