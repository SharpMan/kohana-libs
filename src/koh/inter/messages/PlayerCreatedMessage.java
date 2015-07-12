/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package koh.inter.messages;

import koh.inter.InterMessage;
import koh.inter.MessageEnum;
import koh.protocol.client.Message;
import org.apache.mina.core.buffer.IoBuffer;

/**
 *
 * @author Neo-Craft
 */
public class PlayerCreatedMessage implements InterMessage {

    public static final int ID = 4;

    public int Count, Owner;

    public PlayerCreatedMessage() {

    }

    public PlayerCreatedMessage(int count, int Owner) {
        this.Count = count;
        this.Owner = Owner;
    }

    @Override
    public int getMessageId() {
        return MessageEnum.PlayerCreated.value();
    }

    @Override
    public void serialize(IoBuffer buf) {
        buf.putInt(Count);
        buf.putInt(Owner);
    }

    @Override
    public void deserialize(IoBuffer buf) {
        this.Count = buf.getInt();
        this.Owner = buf.getInt();
    }

}
