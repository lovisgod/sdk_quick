package com.interswitchng.smartpos.shared.services.iso8583.utils;

import android.content.Context;
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.models.posconfig.TerminalConfig
import com.interswitchng.smartpos.shared.models.posconfig.EmvAIDs
import org.simpleframework.xml.core.Persister

import java.io.IOException;
import java.io.InputStream;

object FileUtils {

    internal fun <T> readXml(clazz: Class<T>, xmlFile: InputStream): T {
        val deserializer = Persister()
        return deserializer.read(clazz, xmlFile)
    }

    internal fun getFilesArrayFromAssets(context: Context, path: String): Array<String>? {
        val assetManager = context.resources.assets
        var files: Array<String>? = null
        try {
            files = assetManager.list(path);
        } catch (e: IOException) {
            e.printStackTrace()
        }

        if(files != null) {
            for (i in 0 until files.size) {
                files[i] = path + "/" + files[i]
            }
        }

        return files;
    }


    internal fun getFromAssets(context: Context): ByteArray? {
        var ins: InputStream? = null
        try {
            ins = context.resources.assets.open("jpos.xml");

            val length = ins.available()
            val buffer = ByteArray(length)
            ins.read(buffer)
            return buffer
        } catch (e: Exception) {
            e.printStackTrace();
        } finally {
            if (ins != null) {
                try {
                    ins.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }

    internal fun getAids(xmlFile: InputStream): EmvAIDs {
        val deserializer = Persister()
        return deserializer.read(EmvAIDs::class.java, xmlFile)
    }

    internal fun getTerminalConfig(xmlFile: InputStream): TerminalConfig {
        val deserializer = Persister()
        return deserializer.read(TerminalConfig::class.java, xmlFile)
    }

    fun getConfigurations(context: Context): Pair<TerminalConfig, EmvAIDs> {
        // get resource streams
        val emvStr = context.resources.openRawResource(R.raw.isw_emv_config)
        val terminalStr = context.resources.openRawResource(R.raw.isw_terminal_config)
        // extract values
        val emvApps = getAids(emvStr)
        val terminalConfig = getTerminalConfig(terminalStr)

        return Pair(terminalConfig, emvApps)
    }
}
