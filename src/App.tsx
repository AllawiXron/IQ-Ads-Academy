/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { Menu, X, Landmark, TrendingUp, Sparkles, PhoneCall, CheckCircle, Award, Home, Coins, MessageSquare, BookOpen } from 'lucide-react';

// Modular components
import Hero from './components/Hero';
import Calculator from './components/Calculator';
import PitchTemplates from './components/PitchTemplates';
import ProductInfo from './components/ProductInfo';
import FAQ from './components/FAQ';

export default function App() {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [scrolled, setScrolled] = useState(false);
  const [activeTab, setActiveTab] = useState('hero');

  // Monitor scroll state for floating header
  useEffect(() => {
    const handleScroll = () => {
      if (window.scrollY > 30) {
        setScrolled(true);
      } else {
        setScrolled(false);
      }
      
      // Highlight correct bottom tab on scroll
      const sections = ['hero', 'calculator', 'toolkit'];
      for (const section of sections) {
        const el = document.getElementById(section);
        if (el) {
          const rect = el.getBoundingClientRect();
          if (rect.top <= 200 && rect.bottom >= 150) {
            setActiveTab(section);
            break;
          }
        }
      }
    };
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  const scrollToSection = (id: string) => {
    setMobileMenuOpen(false);
    setActiveTab(id === 'hero-section' ? 'hero' : id);
    const el = id === 'hero' ? document.documentElement : document.getElementById(id);
    if (el) {
      el.scrollIntoView({ behavior: 'smooth' });
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 text-slate-800 font-sans selection:bg-indigo-600 selection:text-white">
      
      {/* Dynamic Floating Header */}
      <nav className={`fixed inset-x-0 top-0 z-40 transition-all duration-300 ${
        scrolled 
          ? 'bg-white/95 border-b border-slate-200/80 shadow-sm backdrop-blur-md py-3.5' 
          : 'bg-white/90 border-b border-slate-100 py-4.5'
      }`}>
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 flex items-center justify-between flex-row-reverse">
          
          {/* Logo & Platform Name */}
          <div className="flex items-center gap-3 flex-row-reverse">
            <div className="h-10 w-10 rounded-lg bg-indigo-600 flex items-center justify-center text-white font-bold text-xl shadow-sm">
              IQ
            </div>
            <div className="text-right">
              <span className="font-bold text-slate-900 text-lg font-sans leading-none block">IQ Ads Academy</span>
              <span className="text-xs text-indigo-600 font-semibold tracking-wide uppercase block mt-0.5">برنامج المروجين الميدانيين 🇮🇶</span>
            </div>
          </div>

          {/* Desktop Navigation Links */}
          <div className="hidden md:flex flex-row-reverse items-center gap-6">
            <button 
              id="nav-calc"
              onClick={() => scrollToSection('calculator')} 
              className="text-slate-600 text-sm font-semibold hover:text-indigo-600 transition-colors cursor-pointer"
            >
              حاسبة أرباحك 💸
            </button>
            <button 
              id="nav-toolkit"
              onClick={() => scrollToSection('toolkit')} 
              className="text-slate-600 text-sm font-semibold hover:text-indigo-600 transition-colors cursor-pointer"
            >
              حقيبة رسائل الترويج 🎒
            </button>
            <button 
              id="nav-about"
              onClick={() => scrollToSection('about-product')} 
              className="text-slate-600 text-sm font-semibold hover:text-indigo-600 transition-colors cursor-pointer"
            >
              تفاصيل الخدمة 🎓
            </button>
            <button 
              id="nav-faq"
              onClick={() => scrollToSection('faq')} 
              className="text-slate-600 text-sm font-semibold hover:text-indigo-600 transition-colors cursor-pointer"
            >
              الأسئلة الشائعة 🙋‍♂️
            </button>
          </div>

          {/* Desktop Right Actions (Affiliate CTA) */}
          <div className="hidden md:flex">
            <button 
              id="btn-nav-join"
              onClick={() => scrollToSection('calculator')}
              className="flex items-center gap-1.5 rounded-full bg-indigo-600 px-6 py-2 text-sm font-bold text-white hover:bg-indigo-700 transition-all cursor-pointer shadow-sm"
            >
              <span>انضم إلينا الآن</span>
            </button>
          </div>

          {/* Mobile Menu Icon */}
          <button 
            id="btn-mobile-menu"
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            className="md:hidden text-slate-600 hover:text-slate-900 p-2 rounded-lg cursor-pointer"
          >
            {mobileMenuOpen ? <X className="h-6 w-6" /> : <Menu className="h-6 w-6" />}
          </button>

        </div>
      </nav>

      {/* Mobile Navigation Drawer */}
      <AnimatePresence>
        {mobileMenuOpen && (
          <motion.div 
            initial={{ opacity: 0, height: 0 }}
            animate={{ opacity: 1, height: 'auto' }}
            exit={{ opacity: 0, height: 0 }}
            transition={{ duration: 0.3 }}
            className="fixed inset-x-0 top-[72px] z-35 bg-white border-b border-slate-200 shadow-xl md:hidden text-right"
          >
            <div className="px-4 py-6 space-y-4">
              <button 
                id="mob-calc"
                onClick={() => scrollToSection('calculator')} 
                className="w-full text-right text-slate-700 py-2.5 font-bold border-b border-slate-100 hover:text-indigo-600 text-sm block cursor-pointer"
              >
                حاسبة أرباحك 💸
              </button>
              <button 
                id="mob-toolkit"
                onClick={() => scrollToSection('toolkit')} 
                className="w-full text-right text-slate-700 py-2.5 font-bold border-b border-slate-100 hover:text-indigo-600 text-sm block cursor-pointer"
              >
                حقيبة رسائل الترويج 🎒
              </button>
              <button 
                id="mob-about"
                onClick={() => scrollToSection('about-product')} 
                className="w-full text-right text-slate-700 py-2.5 font-bold border-b border-slate-100 hover:text-indigo-600 text-sm block cursor-pointer"
              >
                تفاصيل الخدمة 🎓
              </button>
              <button 
                id="mob-faq"
                onClick={() => scrollToSection('faq')} 
                className="w-full text-right text-slate-700 py-2.5 font-bold block hover:text-indigo-600 text-sm cursor-pointer"
              >
                الأسئلة الشائعة 🙋‍♂️
              </button>
            </div>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Content Sections */}
      <main className="relative z-10 pb-20 md:pb-0">
        <Hero />
        <Calculator />
        <PitchTemplates />
        <ProductInfo />
        <FAQ />
      </main>

      {/* App-like Fixed Bottom Navigation for Mobile Users */}
      <div className="fixed bottom-0 left-0 right-0 z-50 bg-white/95 border-t border-slate-200 shadow-lg md:hidden flex justify-around items-center py-2 px-3 backdrop-blur-md pb-safe">
        <button 
          onClick={() => scrollToSection('hero')}
          className={`flex flex-col items-center justify-center flex-1 py-1 transition-all ${
            activeTab === 'hero' ? 'text-indigo-600 font-bold scale-105' : 'text-slate-400'
          }`}
        >
          <Home className="h-5 w-5 mb-0.5" />
          <span className="text-[10px] font-sans">الرئيسية</span>
        </button>

        
        <button 
          onClick={() => scrollToSection('calculator')}
          className={`flex flex-col items-center justify-center flex-1 py-1 transition-all ${
            activeTab === 'calculator' ? 'text-indigo-600 font-bold scale-105' : 'text-slate-400'
          }`}
        >
          <Coins className="h-5 w-5 mb-0.5" />
          <span className="text-[10px] font-sans">الحاسبة</span>
        </button>

        <button 
          onClick={() => scrollToSection('toolkit')}
          className={`flex flex-col items-center justify-center flex-1 py-1 transition-all ${
            activeTab === 'toolkit' ? 'text-indigo-600 font-bold scale-105' : 'text-slate-400'
          }`}
        >
          <MessageSquare className="h-5 w-5 mb-0.5" />
          <span className="text-[10px] font-sans">الحقيبة</span>
        </button>
      </div>

      {/* Styled Iraqi Footer */}
      <footer className="bg-white border-t border-slate-200 py-12 pb-24 md:pb-12 text-slate-500 text-right">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8 items-center flex-row-reverse text-center md:text-right">
            
            {/* Branding */}
            <div className="space-y-2 flex flex-col items-center md:items-end">
              <div className="flex items-center gap-2 flex-row-reverse">
                <div className="h-8 w-8 bg-indigo-600 rounded-lg flex items-center justify-center text-white font-bold text-sm">
                  IQ
                </div>
                <span className="font-extrabold text-slate-900 text-base font-sans">IQ Ads Academy</span>
              </div>
              <p className="text-xs text-slate-500 leading-relaxed font-sans max-w-xs md:text-right text-center">
                البرنامج التفاعلي العراقي الأول لتعلم إعلانات التمويل وتخفيض ميزانية الصرف وزيادة المبيعات.
              </p>
            </div>

            {/* Quick links to sections */}
            <div className="flex flex-row-reverse flex-wrap justify-center gap-x-6 gap-y-2 text-xs font-sans text-slate-500">
              <button onClick={() => scrollToSection('calculator')} className="hover:text-indigo-600 transition-colors cursor-pointer">الحاسبة</button>
              <button onClick={() => scrollToSection('toolkit')} className="hover:text-indigo-600 transition-colors cursor-pointer">حقيبة الترويج</button>
              <button onClick={() => scrollToSection('about-product')} className="hover:text-indigo-600 transition-colors cursor-pointer">تفاصيل الكورس</button>
            </div>

            {/* Support and copyright */}
            <div className="space-y-2 flex flex-col items-center md:items-start text-center md:text-left">
              <span className="text-xs text-slate-600 font-sans block">Made by ali ahmed, owner of Vizion and IQ Ads Academy</span>
              <p className="text-[11px] text-slate-400 font-sans">
                جميع الحقوق محفوظة © {new Date().getFullYear()} لـ IQ Ads Academy.
              </p>
            </div>

          </div>
        </div>
      </footer>

    </div>
  );
}
