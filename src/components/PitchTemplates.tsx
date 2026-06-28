/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import { useState } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { Clipboard, Check, Share2, MessageSquare, AlertCircle, Copy } from 'lucide-react';
import { PITCH_TEMPLATES } from '../data';
import { PitchTemplate } from '../types';

export default function PitchTemplates() {
  const [activeCategory, setActiveCategory] = useState<string>('جميع النصوص');
  const [copiedId, setCopiedId] = useState<string | null>(null);

  const handleCopy = (id: string, text: string) => {
    navigator.clipboard.writeText(text);
    setCopiedId(id);
    setTimeout(() => {
      setCopiedId(null);
    }, 2500);
  };

  // Extract unique categories
  const categories = ['جميع النصوص', ...Array.from(new Set(PITCH_TEMPLATES.map(t => t.category)))];

  const filteredTemplates = activeCategory === 'جميع النصوص' 
    ? PITCH_TEMPLATES 
    : PITCH_TEMPLATES.filter(t => t.category === activeCategory);

  return (
    <section id="toolkit" className="bg-white py-20 text-right relative border-b border-slate-200/50">
      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        
        {/* Section Header */}
        <div className="mx-auto max-w-3xl text-center mb-16 space-y-4">
          <h2 className="text-3xl font-extrabold tracking-tight sm:text-4xl text-slate-900 font-sans">
            🎒 حقيبة المروج الذكية (رسائل جاهزة للنسخ)
          </h2>
          <p className="text-slate-600 text-lg leading-relaxed font-sans">
            ما يحتاج تتعب نفسك وتفكر بـ نصوص ترويجية! وفرنا الك نصوص رسائل جاهزة ومصممة بدقة باللهجة العراقية اللطيفة والمقنعة ومجربة مسبقاً مع أصحاب المتاجر. فقط انسخ وارسل:
          </p>
          <div className="mx-auto h-1 w-20 rounded-full bg-indigo-600" />
        </div>

        {/* Filter Categories */}
        <div className="flex flex-row-reverse overflow-x-auto gap-2 mb-10 pb-3 -mx-4 px-4 scrollbar-none md:flex-wrap md:justify-center md:pb-0 md:mx-0 md:px-0">
          {categories.map((cat) => (
            <button
              key={cat}
              onClick={() => setActiveCategory(cat)}
              className={`rounded-full px-5 py-2.5 text-xs font-bold transition-all border cursor-pointer shrink-0 whitespace-nowrap ${
                activeCategory === cat
                  ? 'bg-indigo-600 text-white border-indigo-500 shadow-sm'
                  : 'bg-slate-100 text-slate-600 border-slate-200 hover:border-slate-300'
              }`}
            >
              {cat}
            </button>
          ))}
        </div>

        {/* Templates Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
          {filteredTemplates.map((template: PitchTemplate) => (
            <div 
              key={template.id}
              className="rounded-3xl border border-slate-200 bg-white p-6 md:p-8 flex flex-col justify-between shadow-sm relative overflow-hidden group hover:border-indigo-400 transition-all"
            >
              {/* Top border decor */}
              <div className="absolute top-0 right-0 left-0 h-1 bg-gradient-to-l from-indigo-500/10 to-transparent" />
              
              <div className="space-y-4">
                <div className="flex flex-row-reverse items-center justify-between">
                  <span className="rounded-lg bg-indigo-50 border border-indigo-100 px-3 py-1 text-[10px] font-bold text-indigo-700">
                    {template.category}
                  </span>
                  <div className="flex items-center gap-1.5 text-slate-400 text-xs font-mono">
                    <MessageSquare className="h-3.5 w-3.5" />
                    <span>سيناريو رسائل جاهز</span>
                  </div>
                </div>

                <h3 className="text-xl font-bold text-slate-900 font-sans">
                  {template.title}
                </h3>

                <p className="text-xs text-slate-500 font-sans leading-relaxed">
                  {template.description}
                </p>

                {/* Selling Angle */}
                <div className="rounded-xl bg-amber-50/50 border border-amber-150 p-3 flex flex-row-reverse items-start gap-2.5 text-xs text-slate-700">
                  <AlertCircle className="h-4 w-4 text-amber-600 shrink-0 mt-0.5" />
                  <p className="font-sans leading-relaxed">
                    <strong className="text-amber-800 font-bold">الزاوية التسويقية للإقناع:</strong> {template.angle}
                  </p>
                </div>

                {/* Script Box */}
                <div className="relative pt-4">
                  <textarea
                    readOnly
                    value={template.script}
                    className="w-full h-64 rounded-2xl bg-slate-50 border border-slate-200 px-4 py-4 text-xs font-sans text-slate-700 leading-relaxed text-right resize-none focus:outline-none focus:border-slate-300 select-all"
                  />
                  
                  {/* Copy overlay button */}
                  <div className="absolute bottom-4 left-4 flex flex-row-reverse gap-2">
                    <button
                      id={`btn-copy-${template.id}`}
                      onClick={() => handleCopy(template.id, template.script)}
                      className="flex items-center gap-1.5 rounded-full bg-white border border-slate-200 px-3.5 py-2 text-xs font-bold text-slate-600 hover:text-slate-800 hover:bg-slate-50 hover:border-slate-300 transition-all cursor-pointer shadow-sm"
                    >
                      {copiedId === template.id ? (
                        <>
                          <Check className="h-3.5 w-3.5 text-indigo-600" />
                          <span className="text-indigo-600 font-sans">تم نسخ الرسالة!</span>
                        </>
                      ) : (
                        <>
                          <Copy className="h-3.5 w-3.5" />
                          <span className="font-sans">نسخ النص</span>
                        </>
                      )}
                    </button>

                    <a
                      href="instagram://direct"
                      target="_blank"
                      rel="noopener noreferrer"
                      onClick={(e) => {
                        // If native app scheme doesn't load within half second, redirect to web DM
                        setTimeout(() => {
                          window.open("https://www.instagram.com/direct/inbox/", "_blank");
                        }, 500);
                      }}
                      className="flex items-center gap-1.5 rounded-full bg-slate-50 border border-slate-200 px-3 py-2 text-xs font-bold text-slate-600 hover:bg-indigo-50 hover:text-indigo-700 hover:border-indigo-200 transition-all cursor-pointer shadow-sm"
                      title="افتح إنستغرام دايركت"
                    >
                      <span>إنستغرام 📸</span>
                    </a>
                  </div>
                </div>
              </div>

              {/* Tips for sending */}
              <div className="mt-6 border-t border-slate-100 pt-4 flex flex-row-reverse items-center justify-between text-xs text-slate-400 font-sans">
                <span>تعديل الاسم والتحية عند الإرسال</span>
                <span className="text-slate-600 font-bold">انسخ وارسل دايركت (DM) 🚀</span>
              </div>

            </div>
          ))}
        </div>

        {/* Copy Floating Toast Notifications */}
        <AnimatePresence>
          {copiedId && (
            <motion.div
              initial={{ opacity: 0, y: 50, x: '-50%' }}
              animate={{ opacity: 1, y: 0, x: '-50%' }}
              exit={{ opacity: 0, y: 50, x: '-50%' }}
              className="fixed bottom-24 md:bottom-8 left-1/2 z-50 rounded-2xl bg-indigo-600 px-6 py-4 text-white font-bold text-xs sm:text-sm shadow-xl flex items-center gap-3 font-sans border border-indigo-500 w-[calc(100%-2rem)] max-w-md justify-center text-center"
            >
              <Check className="h-5 w-5 bg-white text-indigo-600 rounded-full p-0.5 animate-bounce" />
              <span>تم النسخ بنجاح! تكدر تفتح الانستغرام هسة وترسله للصفحة مباشرة 🚀</span>
            </motion.div>
          )}
        </AnimatePresence>

      </div>
    </section>
  );
}
