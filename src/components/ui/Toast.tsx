'use client';
import { useEffect, useState } from 'react';
import { CheckCircle, AlertCircle, X } from 'lucide-react';

interface ToastProps {
  message: string;
  type?: 'success' | 'error';
  onClose: () => void;
  duration?: number;
}

export default function Toast({ message, type = 'success', onClose, duration = 3000 }: ToastProps) {
  const [isVisible, setIsVisible] = useState(true);

  useEffect(() => {
    const timer = setTimeout(() => {
      setIsVisible(false);
      setTimeout(onClose, 300);
    }, duration);
    return () => clearTimeout(timer);
  }, [duration, onClose]);

  return (
    <div className={`fixed top-4 right-4 z-50 flex items-center gap-3 glass px-4 py-3 transition-all duration-300 ${isVisible ? 'opacity-100 translate-y-0' : 'opacity-0 -translate-y-4'}`}>
      {type === 'success' ? (
        <CheckCircle className="text-primary-500" size={20} />
      ) : (
        <AlertCircle className="text-red-500" size={20} />
      )}
      <p className="text-sm text-dark-200">{message}</p>
      <button onClick={onClose} className="text-dark-400 hover:text-dark-200">
        <X size={16} />
      </button>
    </div>
  );
}
